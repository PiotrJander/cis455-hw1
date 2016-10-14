package edu.upenn.cis.cis455.webserver.servlet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class UrlPatternWildcard extends UrlPattern {

    private String path;
    private Pattern pattern;

    UrlPatternWildcard(String path) {
        this.path = path.substring(0, path.length() - 2);
        pattern = Pattern.compile(String.format("%s(?<info>/.*$)", this.path));
    }

    @Override
    public Match match(String path) {
        Matcher m = pattern.matcher(path);
        if (m.matches()) {
            return new Match(this.path, m.group("info"));
        } else if (this.path.equals(path)) {
            return new Match(this.path);
        } else {
            return null;
        }
    }
}
