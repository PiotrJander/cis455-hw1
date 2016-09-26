package edu.upenn.cis.cis455.webserver;

class HttpRequest {
    private boolean ok = true;
    private HttpMethod method;
    private String path;
    private HttpVersion version;

    public HttpMethod getMethod() {
        return method;
    }

    void setMethod(String method) throws IllegalArgumentException {
            this.method = HttpMethod.valueOf(method);
    }

    String getPath() {
        return path;
    }

    void setPath(String path) {
        this.path = path;
    }

    void markAsBad() {
        ok = false;
    }

    boolean isOk() {
        return ok;
    }

    void setVersion(String version) throws IllegalArgumentException {
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

    HttpResponse makePartialResponse() {
        HttpResponse response = new HttpResponse();
        response.setVersion(version);

        if (this.isOk()) {
            response.setStatus(HttpStatus.OK);
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST);
        }

        return response;
    }
}

enum HttpMethod {
    GET,
    HEAD,
    POST;
}

enum HttpVersion {
    ONE_0,
    ONE_1;
}