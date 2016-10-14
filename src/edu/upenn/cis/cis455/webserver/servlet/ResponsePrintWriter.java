package edu.upenn.cis.cis455.webserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

class ResponsePrintWriter extends PrintWriter {

    private HttpServletResponse observer;

    ResponsePrintWriter(Writer out, HttpServletResponse observer) {
        super(out);
        this.observer = observer;
    }


    /**
     * Flushes the stream.
     *
     * @see #checkError()
     */
    @Override
    public void flush() {
        super.flush();
        observer.notifyFlush();
    }
}
