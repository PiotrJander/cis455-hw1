package edu.upenn.cis.cis455.webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

class Task {
    private Socket socket;
    private HttpRequest request;
    private HttpResponse response;
    private Path filePath;

    Task(TcpRequest tcpRequest) {
        this.socket = tcpRequest.getSocket();
    }

    void run() throws IOException {
        try (
            OutputStream binaryOut = socket.getOutputStream();
            PrintWriter out = new PrintWriter(binaryOut, true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()))
        ) {
            processRequest(binaryOut, out, in);
        } finally {
            socket.close();
        }
    }

    private void processRequest(OutputStream binaryOut, PrintWriter out, BufferedReader in) throws IOException {
        try {
            request = new HttpRequest(in);
            response = new HttpResponse(request);
            response.checkForBadRequest(request);
            handleSpecialRequests();
            setPath();
            getItem();
            response.send();
        } catch (SendHttpResponseException e) {
            response.sendOverSocket(binaryOut, out);
        }
    }

    private void handleSpecialRequests() {
        switch (request.getPath()) {
            case "/shutdown":
                HttpServer.stop();
                return;
            case "/control":
                controlPage();
        }
    }

    private void controlPage() {
        throw new UnsupportedOperationException();
    }

    private void setPath() throws SendHttpResponseException {
        Path root = HttpServer.getRootDirectory();
        try {
            // TODO how come path is good here? no need to strip the leading slash?
            filePath = root.resolve(request.getPath()).toRealPath(LinkOption.NOFOLLOW_LINKS);
            if (!filePath.startsWith(root)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException | IOException e) {
            response.error(HttpStatus.BAD_REQUEST).send();
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
        throw new UnsupportedOperationException();
    }
}
