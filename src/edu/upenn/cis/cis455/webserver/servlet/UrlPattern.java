package edu.upenn.cis.cis455.webserver.servlet;

public abstract class UrlPattern {

    public static UrlPattern make(String path) {
        if (path.endsWith("/*")) {
            return new UrlPatternWildcard(path);
        } else {
            return new UrlPatternLiteral(path);
        }
    }

    public abstract Match match(String path);
}
