package edu.upenn.cis.cis455.webserver;

import java.io.BufferedReader;
import java.io.IOException;

class HttpParser {
    private HttpRequest request;
    private BufferedReader in;

    HttpParser(BufferedReader in) {
        this.in = in;
        request = new HttpRequest();
    }

    HttpRequest parse() throws IOException {
        String[] first = in.readLine().split("\\s+");

        if (first.length < 3) {
            request.markAsBad();
        }

        request.setPath(first[1]);

        try {
            request.setMethod(first[0]);
            request.setVersion(first[2]);
        } catch (IllegalArgumentException e) {
            request.markAsBad();
        }

        in.readLine();
        in.readLine();

        return request;
    }
}
