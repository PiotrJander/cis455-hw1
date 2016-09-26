package edu.upenn.cis.cis455.webserver;

class HtmlTemplate {
    private String title;
    private String body;

    HtmlTemplate(String title, String body) {
        this.title = title;
        this.body = body;
    }

    @Override
    public String toString() {
        return
            "<!doctype html>\n" +
            "<html lang=\"en\">\n" +
                "<head>\n" +
                    "<title>" + title + "</title>\n" +
                "</head>\n" +
                "<body>\n" +
                    body +
                "</body>\n" +
            "</html>\n";
    }

    void appendToBody(String markup) {
        body += markup;
    }
}
