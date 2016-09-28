package edu.upenn.cis.cis455.webserver;

enum HttpStatus {
    CONTINUE("100", "Continue"),
    OK("200", "OK"),
    BAD_REQUEST("400", "Bad Request"),
    NOT_FOUND("404", "Not Found"),
    INTERNAL_SERVER_ERROR("500", "Internal Server Error"),
    NOT_IMPLEMENTED("501", "Not Implemented");

    private final String statusCode;
    private final String name;

    HttpStatus(String statusCode, String name) {
        this.statusCode = statusCode;
        this.name = name;
    }

    String getStatusCode() {
        return statusCode;
    }

    String getName() {
        return name;
    }
}
