package edu.upenn.cis.cis455.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

class HttpResponse {
    private HttpRequest request;

    private HttpMethod method;
    private HttpVersion version;
    private HttpStatus status;

    private HashMap<String, String> httpResponseHeaders = new HashMap<>();

    private HttpResponsePayload payload;

    HttpResponse(HttpRequest req) {
        this.request = req;

        this.status = HttpStatus.OK;
        this.version = req.getVersion();
        this.method = req.getMethod();
    }

    void checkForBadRequest() throws SendHttpResponseException {
        if (!request.isOk()) {
            error(HttpStatus.BAD_REQUEST).send();
        } else if (request.hasServerError()) {
            error(HttpStatus.INTERNAL_SERVER_ERROR).send();
        } else if (request.getMethod() != HttpMethod.GET && request.getMethod() != HttpMethod.HEAD) {
            error(HttpStatus.NOT_IMPLEMENTED).send();
        }
    }

    void initializeHeaders() {
        addResponseHeader("Server", "Piotr's server");
        addResponseHeader("Connection", "close");
        addResponseHeader("Date", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
    }

    private void addResponseHeader(String name, String value) {
        httpResponseHeaders.put(name, value);
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

        // add
        // Last-Modified
        // Content-Type
        // Content-Length

        // GET requests
        if (method == HttpMethod.HEAD)  return;

        if (payload instanceof StringPayload) {
            out.println(((StringPayload) payload).getPayload());
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
    private final byte[] payload;

    BinaryPayload(byte[] payload) {
        this.payload = payload;
    }

    byte[] getPayload() {
        return payload;
    }
}