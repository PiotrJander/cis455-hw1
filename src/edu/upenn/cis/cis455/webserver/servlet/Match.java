package edu.upenn.cis.cis455.webserver.servlet;

class Match {

    private String servletPath;
    private String pathInfo;

    Match(String servletPath) {
        this.servletPath = servletPath;
    }

    Match(String servletPath, String pathInfo) {
        this.servletPath = servletPath;
        this.pathInfo = pathInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Match match = (Match) o;

        if (!servletPath.equals(match.servletPath)) return false;
        return pathInfo != null ? pathInfo.equals(match.pathInfo) : match.pathInfo == null;

    }
}
