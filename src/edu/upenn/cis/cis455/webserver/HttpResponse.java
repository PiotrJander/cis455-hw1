package edu.upenn.cis.cis455.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    private HttpRequest request;

    private HttpMethod method;
    private HttpVersion version;
    private HttpStatus status;

    private Map<String, List<String>> httpResponseHeaders = new HashMap<>();

    private byte[] payload = new byte[0];

    public HttpResponse() {}

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
        setHeader("Server", "Piotr's server");
        setHeader("Connection", "close");
        setHeader("Date", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
    }

    public boolean containsHeader(String s) {
        return httpResponseHeaders.containsKey(s);
    }

    public void setHeader(String name, String value) {
        httpResponseHeaders.put(name, Collections.singletonList(value));
    }

    public void addHeader(String name, String value) {
        if (containsHeader(name)) {
            httpResponseHeaders.get(name).add(value);
        } else {
            setHeader(name, value);
        }
    }

    public void resetHeaders() {
        httpResponseHeaders = new HashMap<>();
        initializeHeaders();
    }

    void setContentType(String type) {
        setHeader("Content-Type", type);
    }

    void setLastModified(String time) {
        setHeader("Last-Modified", time);
    }

    public void setPayload(String payload) {
        this.payload = payload.getBytes();
        setHeader("Content-Length", Integer.toString(this.payload.length));
        setContentType("text/html");
    }

    void setPayload(byte[] payload) {
        this.payload = payload;
        setHeader("Content-Length", Integer.toString(payload.length));
    }

    public HttpResponse error(HttpStatus error) {
        status = error;
        return this;
    }

    public HttpResponse error(HttpStatus error, String msg) {
        status = error;
        setPayload((new HtmlTemplate(msg, msg)).toString());
        return this;
    }

    HttpResponse serverError() {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    public void send() throws SendHttpResponseException {
        throw new SendHttpResponseException();
    }

    void sendOverSocket(OutputStream binaryOut, PrintWriter out) throws IOException {
        out.println(getStatusLine());

        // print headers
        for (Map.Entry<String, List<String>> e : httpResponseHeaders.entrySet()) {
            out.println(String.format("%s: %s", e.getKey(), String.join("", e.getValue())));
        }

        // print empty line
        out.println();

        // finish if HEAD request
        if (method == HttpMethod.HEAD) {
            return;
        }

        binaryOut.write(payload);
    }

    private String getStatusLine() {
        return String.format(
                "%s %s %s",
                version.getName(),
                status.getStatusCode(),
                status.getName()
        );
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public HttpRequest getRequest() {
        return request;
    }
}

