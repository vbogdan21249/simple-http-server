package com.vb2.httpserver.core;

import com.vb2.http.HttpParser;
import com.vb2.http.HttpParsingException;
import com.vb2.http.HttpRequest;
import com.vb2.httpserver.core.io.WebRootHandler;
import com.vb2.httpserver.core.io.WebRootNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);

    private Socket socket;
    private String webRoot;

    public HttpConnectionWorkerThread(Socket socket, String webRoot) {
        this.webRoot = webRoot;
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream in = null;
        OutputStream out = null;

        HttpParser requestParser = new HttpParser();

        final String CRLF = "\n\r";

        try {
            LOGGER.info(" * Request processing started.");
            in = this.socket.getInputStream();
            out = this.socket.getOutputStream();
            HttpRequest request = requestParser.parseHttpRequest(in);

            WebRootHandler webRootHandler = new WebRootHandler(webRoot);
            String responseBody = new String(webRootHandler.getFileBytes(request.getRequestTarget()));

            String response = "HTTP/1.1 200 OK" + CRLF
                    + "Content-Length: " + responseBody.length() + CRLF
                    + "Content-Type: " + webRootHandler.getFileMimeType(request.getRequestTarget())
                    + CRLF + CRLF
                    + responseBody
                    + CRLF + CRLF;

            out.write(response.getBytes());
            out.flush();

            LOGGER.info(" * Response sent:\n\n{}", response);
            LOGGER.info(" * Request processing finished.");

        } catch (IOException e) {
            LOGGER.error("Problem with communication: ", e);
            throw new RuntimeException(e);
        } catch (HttpParsingException e) {
            LOGGER.error("Problem with parsing: ", e);
            throw new RuntimeException(e);
        } catch (WebRootNotFoundException e) {
            LOGGER.error("Problem with webroot: ", e);
            throw new RuntimeException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                    LOGGER.info(" * Closing input stream.");
                } catch (IOException ignored) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                    LOGGER.info(" * Closing output stream.");
                } catch (IOException ignored) {
                }
            }
            if (this.socket != null) {
                try {
                    this.socket.close();
                    LOGGER.info(" * Closing socket.");
                } catch (IOException ignored) {
                }
            }
        }

    }
}
