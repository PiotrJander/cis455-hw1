package edu.upenn.cis.cis455.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {
    private BufferedReader in;

    private boolean ok = true;
    private boolean serverError = false;

    private String badRequestErrorMessage;

    private HttpMethod method;
    private URL url;
    private String urlString;
    private HttpVersion version = HttpVersion.ONE_0;

    private HashMap<String, List<String>> httpRequestHeaders = new HashMap<>();

    public HttpRequest() {}

    HttpRequest(BufferedReader in) throws IOException {
        this.in = in;
    }

    void parse() throws IOException {
        try {
            parseFirstLine();
            parseHeaders();

            if (version == HttpVersion.ONE_1) {
                checkHost();
            }
            normalizePath();
        } catch (IOException e) {
            serverError = true;
        } catch (BadRequestException e) {
            ok = false;
            badRequestErrorMessage = e.getErrorMessage();
        }
    }

    private void parseFirstLine() throws IOException, BadRequestException {
        String firstLine = in.readLine();
        String[] first;
        if (firstLine != null) {
            first = firstLine.split("\\s+");
        } else {
            throw new BadRequestException();
        }

        if (first.length < 2) {
            throw new BadRequestException();
        }

        try {
            setMethod(first[0]);
            urlString = first[1];

            if (first.length >= 3) {
                setVersion(first[2]);
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException();
        }
    }

    /**
     * Syntax not semantics here.
     *
     * After execution of this method, the reader will be at the start of the body.
     */
    void parseHeaders() throws IOException, BadRequestException {
        String line;
        while ((line = in.readLine()) != null && !line.equals("")) {
            // parse the header
            Pattern p = Pattern.compile("(?<name>^[\\w-]+):\\s+(?<value>.*$)");
            Matcher m = p.matcher(line);
            if (m.matches()) {
                String name = m.group("name");
                String value = m.group("value");

                // deal with comma-separated values
                List<String> values = new ArrayList<>(Arrays.asList(value.split(",")));
                if (httpRequestHeaders.containsKey(name)) {
                    httpRequestHeaders.get(name).addAll(values);
                } else {
                    httpRequestHeaders.put(name, values);
                }
            } else {
                throw new BadRequestException("Invalid header " + line);
            }
        }
    }

    /**
     * If HTTP version is 1.1, checks that the Host header is present
     */
    private void checkHost() throws BadRequestException {
        String host = getHeaderValue("Host");
        if (host == null) {
            throw new BadRequestException("'Host' header not specified");
        }
    }

    /**
     *
     */
    private void normalizePath() throws BadRequestException {
        try {
            String host = getHeaderValue("Host");

            // relative URLs (urlString, query, fragment) will be resolved relative to the context
            URL context = new URL(String.format("http://%s", host == null ? "placeholder.com" : host));

            URL url = new URL(context, urlString);

            // now if absolute URL was given, the context was overriden; we must check for agreement with the Host header
            if (!Objects.equals(url.getHost(), host)) {
                throw new BadRequestException("'Host' header and the host in the URL don't agree");
            }

            this.url = url;

        } catch (MalformedURLException ignore) {
            // 'urlString' is a urlString, no action required
        }
    }

    private void setMethod(String method) throws IllegalArgumentException {
            this.method = HttpMethod.valueOf(method);
    }

//    String getPath() {
//        return urlString;
//    }

    boolean isOk() {
        return ok;
    }

    boolean hasServerError() { return serverError; }

    private void setVersion(String version) throws IllegalArgumentException {
        switch (version) {
            case "HTTP/1.0":
                this.version = HttpVersion.ONE_0;
                return;
            case "HTTP/1.1":
                this.version = HttpVersion.ONE_1;
                return;
            default:
                throw new IllegalArgumentException();
        }
    }

    public HttpVersion getVersion() {
        return version;
    }

    public HttpMethod getMethod() {
        return method;
    }

    /**
     * Returns the first value for the header (usually there's just one value).
     */
    public String getHeaderValue(String headerName) {
        return httpRequestHeaders.get(headerName).get(0);
    }

    String getBadRequestErrorMessage() {
        return badRequestErrorMessage;
    }

    boolean continueExpected() {
        return Objects.equals(getHeaderValue("Expect"), "100-continue");
    }

    public HashMap<String, List<String>> getHeaders() {
        return httpRequestHeaders;
    }

    public void setHttpRequestHeaders(HashMap<String, List<String>> httpRequestHeaders) {
        this.httpRequestHeaders = httpRequestHeaders;
    }

    public URL getUrl() {
        return url;
    }

    public String getPath() {
        return url.getPath();
    }

    public BufferedReader getIn() {
        return in;
    }
}

class BadRequestException extends Exception {
    private String errorMessage;

    BadRequestException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    BadRequestException() {}

    String getErrorMessage() {
        return errorMessage;
    }
}
