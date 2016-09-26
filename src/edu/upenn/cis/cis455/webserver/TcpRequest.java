package edu.upenn.cis.cis455.webserver;

import java.net.Socket;

class TcpRequest {
    private Socket socket;

    TcpRequest(Socket socket) {
        this.socket = socket;
    }

    Socket getSocket() {
        return socket;
    }
}
