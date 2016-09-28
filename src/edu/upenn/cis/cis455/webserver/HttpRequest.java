package edu.upenn.cis.cis455.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HttpRequest {
    private boolean ok = true;
    private HttpMethod method;
    private String path;
    private HttpVersion version = HttpVersion.ONE_1;
    private HashMap<String, String> httpHeaders = new HashMap<>();

    HttpRequest(BufferedReader in) throws IOException {
        parseFirstLine(in);
        parseHeaders(in);
    }

    private void parseHeaders(BufferedReader in) throws IOException {
        String line;
        while (!(line = in.readLine()).equals("\n")) {
            // parse the header
            Pattern p = Pattern.compile("(?<name>^[\\w-]):\\s+(?<value>.*)\\n");
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

    private void parseFirstLine(BufferedReader in) throws IOException {
        String[] first = in.readLine().split("\\s+");

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