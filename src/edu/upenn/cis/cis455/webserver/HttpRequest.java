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

    private HttpMethod method;
    private String path;
    private HttpVersion version = HttpVersion.ONE_1;

    private HashMap<String, String> httpHeaders = new HashMap<>();

    HttpRequest(BufferedReader in) throws IOException {
        this.in = in;
    }

    void parse() throws IOException {
        parseFirstLine();
        parseHeaders();

        if (version == HttpVersion.ONE_1) {
            checkHost();
            normalizePath();
        }
    }

    /**
     * If HTTP version is 1.1, checks that the Host header is present
     */
    private void checkHost() {
        String host = getHeaderValue("Host");
        if (host == null) {
            markAsBad();
        }
    }

    private void normalizePath() {
        try {
            URL url = new URL(path);
            String host = getHeaderValue("Host");
            if (!Objects.equals(url.getHost(), host)) {
                markAsBad();
            }
            path = url.getPath();
        } catch (MalformedURLException ignore) {
            // 'path' is a path, no action required
        }
    }

    /**
     * Syntax not semantics here.
     */
    void parseHeaders() throws IOException {
        String line;
        while ((line = in.readLine()) != null && !line.equals("\n")) {
            // parse the header
            Pattern p = Pattern.compile("(?<name>^[\\w-]+):\\s+(?<value>.*$)");
            Matcher m = p.matcher(line);
            if (m.matches()) {
                String name = m.group("name");
                String value = m.group("value");
                httpHeaders.put(name, value);
            } else {
                markAsBad();
            }
        }
    }

    private void parseFirstLine() throws IOException {
        String firstLine = in.readLine();
        String[] first;
        if (firstLine != null) {
            first = firstLine.split("\\s+");
        } else {
            markAsBad();
            return;
        }

        if (first.length < 2) {
            markAsBad();
            return;
        }

        try {
            setMethod(first[0]);
            path = first[1];

            if (first.length >= 3) {
                setVersion(first[2]);
            }
        } catch (IllegalArgumentException e) {
            markAsBad();
        }
    }

    private void setMethod(String method) throws IllegalArgumentException {
            this.method = HttpMethod.valueOf(method);
    }

    String getPath() {
        return path;
    }

    private void markAsBad() {
        ok = false;
    }

    boolean isOk() {
        return ok;
    }

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
        return httpHeaders.get(headerName);
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