package edu.upenn.cis.cis455.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

class HttpResponse {
    private HttpVersion version;
    private HttpStatus status;
    private HttpResponsePayload payload;

    HttpResponse(HttpRequest req) {
        this.status = HttpStatus.OK;
        this.version = req.getVersion();
    }

    void checkForBadRequest(HttpRequest request) throws SendHttpResponseException {
        if (!request.isOk()) {
            error(HttpStatus.BAD_REQUEST).send();
        }
    }

    void setPayload(String payload) {
        this.payload = new StringPayload(payload);
    }

    void setPayload(byte[] payload) {
        this.payload = new BinaryPayload(payload);
    }

    HttpResponse error(HttpStatus error) {
        status = error;
        return this;
    }

    void send() throws SendHttpResponseException {
        throw new SendHttpResponseException();
    }

    void sendOverSocket(OutputStream binaryOut, PrintWriter out) throws IOException {
        out.println(getStatusLine());
        out.println();
        if (payload instanceof StringPayload) {
            out.print(((StringPayload) payload).getPayload());
        } else if (payload instanceof BinaryPayload) {
            binaryOut.write(((BinaryPayload) payload).getPayload());
        }
    }

    private String getStatusLine() {
        return String.format(
                "%s %s %s",
                version.getName(),
                status.getStatusCode(),
                status.getName()
        );
    }
}

class SendHttpResponseException extends Exception {}

interface HttpResponsePayload {}

class StringPayload implements HttpResponsePayload {
    private String payload;

    StringPayload(String payload) {
        this.payload = payload;
    }

    String getPayload() {
        return payload;
    }
}

class BinaryPayload implements HttpResponsePayload {
    private byte[] payload;

    BinaryPayload(byte[] payload) {
        this.payload = payload;
    }

    byte[] getPayload() {
        return payload;
    }
}