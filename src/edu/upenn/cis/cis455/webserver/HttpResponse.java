package edu.upenn.cis.cis455.webserver;

class HttpResponse {
    private HttpVersion version;
    private HttpStatus status;
    private String payload;

    void setVersion(HttpVersion version) {
        this.version = version;
    }

    void setStatus(HttpStatus status) {
        this.status = status;
    }

    void setPayload(String payload) {
        this.payload = payload;
    }
}

enum HttpStatus {
    OK("200"),
    BAD_REQUEST("400");

    private final String statusCode;

    HttpStatus(String statusCode) {
        this.statusCode = statusCode;
    }
}
