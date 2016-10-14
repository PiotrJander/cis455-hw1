package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.servlet.HttpServletRequest;
import edu.upenn.cis.cis455.webserver.servlet.HttpServletResponse;
import edu.upenn.cis.cis455.webserver.servlet.PatternServletPair;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Task {
    private static Logger log = Logger.getLogger(Task.class);

    private Worker worker;

    private Socket socket;
    private HttpRequest request;
    private HttpResponse response;
    private Path filePath;

    Task(TcpRequest tcpRequest, Worker worker) {
        this.socket = tcpRequest.getSocket();
        this.worker = worker;
    }

    void run() throws IOException {
        try (
            OutputStream binaryOut = socket.getOutputStream();
            PrintWriter out = new PrintWriter(binaryOut, true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()))
        ) {
            log.info("Processing request starts");
            processRequest(binaryOut, out, in);
        } finally {
            socket.close();
        }
    }

    private void processRequest(OutputStream binaryOut, PrintWriter out, BufferedReader in) throws IOException {
        try {
            request = new HttpRequest(in);
            request.parse();

            if (request.continueExpected()) {
                sendContinueResponse(binaryOut, out);
            }

            // TODO refactor to account for servlets
            worker.setCurrentRequestPath(request.getPath());

            response = new HttpResponse(request);
            response.initializeHeaders();
            response.checkForBadRequest();
            handleSpecialRequests();

            PatternServletPair servletPair = HttpServer.getApplication().getMatchingServlet(request.getUrl().getPath());
            if (servletPair == null) {
                // no matching pattern for servlet
                handleStaticItems();
            } else {
                handleServletRequest(servletPair, out);
            }
        } catch (SendHttpResponseException e) {
            response.sendOverSocket(binaryOut, out);
            log.info("Response sent");
        }
    }

    private void handleServletRequest(PatternServletPair servletPair, PrintWriter out) throws IOException {
        HttpServlet servlet = servletPair.getServlet();
        HttpServletRequest servletRequest = new HttpServletRequest(socket, request, servletPair.getMatch());
        HttpServletResponse servletResponse = new HttpServletResponse(response, out);
        try {
            servlet.service(servletRequest, servletResponse);
        } catch (ServletException e) {
            // TODO add to error log
            e.printStackTrace();
        }
    }

    private void handleStaticItems() throws SendHttpResponseException {
        setPath();
        getItem();
        response.send();
    }

    private void sendContinueResponse(OutputStream binaryOut, PrintWriter out) throws IOException {
        HttpContinueResponse continueResponse = new HttpContinueResponse(request);
        continueResponse.sendOverSocket(binaryOut, out);
    }

    private void handleSpecialRequests() throws SendHttpResponseException {
        switch (request.getPath()) {
            case "/shutdown":
                log.warn("Shutdown message received");
                HttpServer.stop();
                response.send();
            case "/control":
                controlPage();
                response.send();
        }
    }

    private void controlPage() {
        HtmlTemplate html = new HtmlTemplate("Control Panel", "<h1>Control Panel</h1>");
        html.appendToBody("<p>Piotr Jander<br> piotr@sas.upenn.edu</p>");
        html.appendToBody("<h2>Thread pool</h2>" + "<dl>");
        for (Worker worker : HttpServer.getWorkersPool()) {
            html.appendToBody("<dt>" + worker.getWorkerId() + "</dt>");
            String path = worker.getCurrentRequestPath();
            String status = path == null ? "<i>waiting</i>" : path;
            html.appendToBody("<dd>" + status + "</dd>");
        }
        html.appendToBody("</dl>");
        html.appendToBody("<a href='/shutdown'><button>Shutdown server</button></a>");
        response.setPayload(html.toString());
    }

    private void setPath() throws SendHttpResponseException {
        Path root = HttpServer.getRootDirectory();
        try {
            // TODO how come path is good here? no need to strip the leading slash?
            filePath = root.resolve(request.getPath().substring(1)).toRealPath(LinkOption.NOFOLLOW_LINKS);
            if (!filePath.startsWith(root)) {
                // TODO why this exception?
                throw new IllegalArgumentException();
            }
        } catch (InvalidPathException | IOException e) {
            response.error(HttpStatus.NOT_FOUND).send();
        }
    }

    private void getItem() throws SendHttpResponseException {
        if (Files.isDirectory(filePath)) {
            getDirectoryListing();
        } else if (Files.isRegularFile(filePath)) {
            getFile();
        } else {
            // file doesn't exist
            response.error(HttpStatus.NOT_FOUND).send();
        }
    }

    private void getFile() throws SendHttpResponseException {
        try {
            response.setPayload(Files.readAllBytes(filePath));
            response.setContentType(getMimeType(String.valueOf(filePath)));
            setLastModified();
        } catch (IOException e) {
            response.error(HttpStatus.INTERNAL_SERVER_ERROR).send();
        }
    }

    private void setLastModified() {
        File file = new File(String.valueOf(filePath));
        Instant i = Instant.ofEpochMilli(file.lastModified());
        ZonedDateTime date = ZonedDateTime.ofInstant(i, ZoneOffset.UTC);
        response.setLastModified(date.format(DateTimeFormatter.RFC_1123_DATE_TIME));
    }

    static String getMimeType(String filePath) throws IOException {
        Pattern p = Pattern.compile(".*\\.(?<ext>\\w+$)");
        Matcher m = p.matcher(filePath);
        if (m.matches()) {
            String extension = m.group("ext");
            switch (extension) {
                case "txt":
                    return "text/plain";
                case "html":
                    return "text/html";
                case "jpg":
                    return "image/jpeg";
                case "jpeg":
                    return "image/jpeg";
                case "png":
                    return "image/png";
                case "gif":
                    return "image/gif";
                default:
                    return "application/octet-stream";
            }
        }

        return "application/octet-stream";
    }

    private void getDirectoryListing() {
        File directory = filePath.toFile();
        File[] files = directory.listFiles();
        HtmlTemplate html = new HtmlTemplate(filePath.toString(), "<ol>");
        if (files != null) {
            for (File file : files) {
                html.appendToBody("<li>" + file.getName() + "</li>");
            }
        }
        html.appendToBody("</ol>\n");
        response.setPayload(html.toString());
    }
}
