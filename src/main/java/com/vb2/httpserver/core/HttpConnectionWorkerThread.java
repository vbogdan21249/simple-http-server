package com.vb2.httpserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);

    private Socket socket;

    public HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        OutputStream out = null;

        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = this.socket.getOutputStream();

            String clientRequest = in.readLine();
            LOGGER.info(clientRequest);

            String html = "<html>" +
                    "<head><title>TEST</title></head>" +
                    "<body><h1>HELLO</h1></body>" +
                    "</html>";
            final String CRLF = "\n\r";

            String response = "HTTP/1.1 200 OK" + CRLF
                    + "Content-Length: " + html.getBytes().length
                    + CRLF + CRLF
                    + html
                    + CRLF + CRLF;

            out.write(response.getBytes());
            out.flush();


            LOGGER.info(" * Processing finished.");
        } catch (IOException e) {
            LOGGER.error("Problem with communication: ", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }

            }
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException ignored) {
                }
            }
        }

    }
}
