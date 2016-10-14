package edu.upenn.cis.cis455.webserver.servlet;

import javax.servlet.ServletInputStream;
import java.io.BufferedReader;
import java.io.IOException;

public class ServletInputStreamImpl extends ServletInputStream {

    private BufferedReader bufferedReader;

    public ServletInputStreamImpl(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    @Override
    public int read() throws IOException {
        return bufferedReader.read();
    }
}
