package com.vb2.httpserver.core;

import com.vb2.httpserver.core.io.WebRootHandler;
import com.vb2.httpserver.core.io.WebRootNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListenerThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

    private int port;
    private String webroot;
    private ServerSocket serverSocket;

    public ServerListenerThread(int port, String webroot) throws IOException {
        this.port = port;
        this.webroot = webroot;
        this.serverSocket = new ServerSocket(this.port);
    }

    @Override
    public void run() {
        try {
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                LOGGER.info(" * Connection is accepted: " + socket.getInetAddress());
                HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(socket, webroot);
                workerThread.start();
            }
        } catch (IOException e) {
            LOGGER.error("Problem with setting socket");
            throw new RuntimeException(e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    LOGGER.info("ServerSocket closed.");
                } catch (IOException e) {
                    LOGGER.error("Problem with closing server socket");
                }
            }
        }
    }
}

