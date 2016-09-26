package edu.upenn.cis.cis455.webserver;

public class HtmlTemplate {
    private String title;
    private String body;

    HtmlTemplate(String title, String body) {
        this.title = title;
        this.body = body;
    }

    String toHtml() {
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
