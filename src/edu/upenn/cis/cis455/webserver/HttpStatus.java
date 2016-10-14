package edu.upenn.cis.cis455.webserver;

public enum HttpStatus {
    CONTINUE(100, "Continue"),
    OK(200, "OK"),
    NOT_MODIFIED(304, "Not Modified"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented");

    private final int statusCode;
    private final String name;

    HttpStatus(int statusCode, String name) {
        this.statusCode = statusCode;
        this.name = name;
    }

    int getStatusCode() {
        return statusCode;
    }

    String getName() {
        return name;
    }

    public static HttpStatus getByCode(int statusCode) {
        for (HttpStatus status : HttpStatus.class.getEnumConstants()) {
            if (status.statusCode == statusCode) {
                return status;
            }
        }
        return null;
    }
}
