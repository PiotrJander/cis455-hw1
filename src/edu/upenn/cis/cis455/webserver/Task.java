package edu.upenn.cis.cis455.webserver;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;

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
            worker.setCurrentRequestPath(request.getPath());
            response = new HttpResponse(request);
            response.initializeHeaders();
            response.checkForBadRequest();
            handleSpecialRequests();
            setPath();
            getItem();
            response.send();
        } catch (SendHttpResponseException e) {
            response.sendOverSocket(binaryOut, out);
        }
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
        } catch (IOException e) {
            response.error(HttpStatus.INTERNAL_SERVER_ERROR).send();
        }
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
