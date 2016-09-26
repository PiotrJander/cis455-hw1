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
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()))
        ) {
            request = (new HttpParser(in)).parse();
            response = request.makePartialResponse();

            // TODO respond with bad request here

            processRequest();

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

    private void processRequest() {
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

    private void getItem() {
        if (Files.isDirectory(filePath)) {
            getFile();
        } else if (Files.isRegularFile(filePath)) {
            getDirectoryListing();
        } else {
            // TODO how to set other errors?
        }
    }

    private void getDirectoryListing() {
    }

    private void getFile() {
    }

    private void controlPage() {
        throw new UnsupportedOperationException();
    }

    private void setPath() {
        Path root = HttpServer.getRootDirectory();
        try {
            filePath = root.resolve(request.getPath()).toRealPath(LinkOption.NOFOLLOW_LINKS);
            if (!filePath.startsWith(root)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException | IOException e) {
            request.markAsBad();
        }
    }
}
