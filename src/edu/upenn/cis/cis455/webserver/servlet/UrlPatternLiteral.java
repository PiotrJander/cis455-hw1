package edu.upenn.cis.cis455.webserver.servlet;

class UrlPatternLiteral extends UrlPattern {

    private String path;

    UrlPatternLiteral(String path) {
        this.path = path;
    }

    @Override
    public Match match(String path) {
        if (this.path.equals(path)) {
            return new Match(path);
        } else {
            return null;
        }
    }
}
