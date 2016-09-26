package edu.upenn.cis.cis455.webserver;

import java.io.BufferedReader;
import java.io.IOException;

class HttpRequest {
    private boolean ok = true;
    private HttpMethod method;
    private String path;
    private HttpVersion version = HttpVersion.ONE_1;

    HttpRequest(BufferedReader in) throws IOException {
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

    public HttpMethod getMethod() {
        return method;
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
}

enum HttpMethod {
    GET,
    HEAD,
    POST;
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