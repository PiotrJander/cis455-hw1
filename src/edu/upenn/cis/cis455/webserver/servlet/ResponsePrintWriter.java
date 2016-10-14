package edu.upenn.cis.cis455.webserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

class ResponsePrintWriter extends PrintWriter {

    private HttpServletResponse observer;

    /**
     * Creates a new PrintWriter.
     *
     * @param out       A character-output stream
     * @param autoFlush A boolean; if true, the <tt>println</tt>,
     *                  <tt>printf</tt>, or <tt>format</tt> methods will
     */
    ResponsePrintWriter(Writer out, boolean autoFlush, HttpServletResponse observer) {
        super(out, autoFlush);
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
