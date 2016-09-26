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

    void run() {
        try (
                OutputStream binaryOut = socket.getOutputStream();
                PrintWriter out = new PrintWriter(binaryOut, true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()))
        ) {
            request = (new HttpParser(in)).parse();
            response = new HttpResponse(request);

            try {
                if (!request.isOk()) {
                    response.error(HttpStatus.BAD_REQUEST).send();
                }

                processRequest();
                response.send();
            } catch (SendHttpResponseException e) {
                response.sendOverSocket(binaryOut, out);
            }

            // look at response payload
            // if file, if string, etc

//            String inputLine, outputLine;
//            KnockKnockProtocol kkp = new KnockKnockProtocol();
//            outputLine = kkp.processInput(null);
//            out.println(outputLine);

//            while ((inputLine = in.readLine()) != null) {
//                outputLine = kkp.processInput(inputLine);
//                out.println(outputLine);
//                if (outputLine.equals("Bye"))
//                    break;
//            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRequest() throws SendHttpResponseException {
        switch (request.getPath()) {
            case "/shutdown":
                HttpServer.stop();
                return;
            case "/control":
                controlPage();
                return;
            default:
                setPath();
                getItem();
        }
    }

    private void getItem() throws SendHttpResponseException {
        if (Files.isDirectory(filePath)) {
            getFile();
        } else if (Files.isRegularFile(filePath)) {
            getDirectoryListing();
        } else {
            // file doesn't exist
            response.error(HttpStatus.NOT_FOUND).send();
        }
    }

    private void getDirectoryListing() {
        throw new UnsupportedOperationException();
    }

    private void getFile() throws SendHttpResponseException {
        try {
            response.setPayload(Files.readAllBytes(filePath));
        } catch (IOException e) {
            response.error(HttpStatus.INTERNAL_SERVER_ERROR).send();
        }
    }

    private void controlPage() {
        throw new UnsupportedOperationException();
    }

    private void setPath() throws SendHttpResponseException {
        Path root = HttpServer.getRootDirectory();
        try {
            filePath = root.resolve(request.getPath()).toRealPath(LinkOption.NOFOLLOW_LINKS);
            if (!filePath.startsWith(root)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException | IOException e) {
            response.error(HttpStatus.BAD_REQUEST).send();
        }
    }
}
