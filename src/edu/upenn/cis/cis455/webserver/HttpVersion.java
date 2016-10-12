package edu.upenn.cis.cis455.webserver;

public enum HttpVersion {
    ONE_0("HTTP/1.0"),
    ONE_1("HTTP/1.1");

    private final String name;

    HttpVersion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

