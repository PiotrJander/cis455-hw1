package edu.upenn.cis.cis455.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HttpRequest {
    private final BufferedReader in;

    private boolean ok = true;
    private boolean serverError = true;

    private String badRequestErrorMessage;

    private HttpMethod method;
    private String path;
    private HttpVersion version = HttpVersion.ONE_1;

    private HashMap<String, String> httpRequestHeaders = new HashMap<>();

    HttpRequest(BufferedReader in) throws IOException {
        this.in = in;
    }

    void parse() throws IOException {
        try {
            parseFirstLine();
            parseHeaders();

            if (version == HttpVersion.ONE_1) {
                checkHost();
                normalizePath();
            }
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
            path = first[1];

            if (first.length >= 3) {
                setVersion(first[2]);
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException();
        }
    }

    /**
     * Syntax not semantics here.
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
                httpRequestHeaders.put(name, value);
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

    private void normalizePath() throws BadRequestException {
        try {
            URL url = new URL(path);
            String host = getHeaderValue("Host");
            if (!Objects.equals(url.getHost(), host)) {
                throw new BadRequestException("'Host' header and the host in the URL don't agree");
            }
            path = url.getPath();
        } catch (MalformedURLException ignore) {
            // 'path' is a path, no action required
        }
    }

    private void setMethod(String method) throws IllegalArgumentException {
            this.method = HttpMethod.valueOf(method);
    }

    String getPath() {
        return path;
    }

//    private void markAsBad() {
//        ok = false;
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

    HttpVersion getVersion() {
        return version;
    }

    HttpMethod getMethod() {
        return method;
    }

    String getHeaderValue(String headerName) {
        return httpRequestHeaders.get(headerName);
    }

    String getBadRequestErrorMessage() {
        return badRequestErrorMessage;
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

enum HttpMethod {
    GET,
    HEAD,
    POST
}

enum HttpVersion {
    ONE_0("HTTP/1.0"),
    ONE_1("HTTP/1.1");

    private final String name;

    HttpVersion(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
}