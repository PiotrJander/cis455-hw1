package edu.upenn.cis.cis455.webserver.servlet;

import javax.servlet.http.HttpServlet;

public class PatternServletPair {
    private UrlPattern pattern;
    private HttpServlet servlet;
    private Match match;

    public PatternServletPair(UrlPattern pattern, HttpServlet servlet) {
        this.pattern = pattern;
        this.servlet = servlet;
    }

    public PatternServletPair(String patternString, HttpServlet servlet) {
        this.pattern = UrlPattern.make(patternString);
        this.servlet = servlet;
    }

    public boolean matches(String path) {
        Match match = pattern.match(path);
        if (match == null) {
            return false;
        } else {
            this.match = match;
            return true;
        }
    }

    public HttpServlet getServlet() {
        return servlet;
    }

    public Match getMatch() {
        return match;
    }
}
