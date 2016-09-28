package edu.upenn.cis.cis455.webserver;

class HttpContinueResponse extends HttpResponse {
    HttpContinueResponse(HttpRequest req) {
        super(req);
        this.setStatus(HttpStatus.CONTINUE);
    }
}
