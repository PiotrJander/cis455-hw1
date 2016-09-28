package edu.upenn.cis.cis455.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

class HttpResponse {
    private HttpRequest request;

    private HttpMethod method;
    private HttpVersion version;
    private HttpStatus status;

    private Map<String, String> httpResponseHeaders = new HashMap<>();

    private byte[] payload = new byte[0];

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

    void setContentType(String type) {
        addResponseHeader("Content-Type", type);
    }

    void setLastModified(String time) {
        addResponseHeader("Last-Modified", time);
    }

    void setPayload(String payload) {
        this.payload = payload.getBytes();
        addResponseHeader("Content-Length", Integer.toString(this.payload.length));
        setContentType("text/html");
    }

    void setPayload(byte[] payload) {
        this.payload = payload;
        addResponseHeader("Content-Length", Integer.toString(payload.length));
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

        // print headers
        for (Map.Entry<String, String> e : httpResponseHeaders.entrySet()) {
            out.println(String.format("%s: %s", e.getKey(), e.getValue()));
        }

        // print empty line
        out.println();

        // finish if HEAD request
        if (method == HttpMethod.HEAD) {
            return;
        }

        binaryOut.write(payload);

//        if (payload instanceof StringPayload) {
//            out.println(((StringPayload) payload).getPayload());
//        } else if (payload instanceof BinaryPayload) {
//            binaryOut.write(((BinaryPayload) payload).getPayload());
//        }
    }

    private String getStatusLine() {
        return String.format(
                "%s %s %s",
                version.getName(),
                status.getStatusCode(),
                status.getName()
        );
    }

    void setStatus(HttpStatus status) {
        this.status = status;
    }
}

class SendHttpResponseException extends Exception {}

//interface HttpResponsePayload {}
//
//class StringPayload implements HttpResponsePayload {
//    private String payload;
//
//    StringPayload(String payload) {
//        this.payload = payload;
//    }
//
//    String getPayload() {
//        return payload;
//    }
//}
//
//class BinaryPayload implements HttpResponsePayload {
//    private final byte[] payload;
//
//    BinaryPayload(byte[] payload) {
//        this.payload = payload;
//    }
//
//    byte[] getPayload() {
//        return payload;
//    }
//}