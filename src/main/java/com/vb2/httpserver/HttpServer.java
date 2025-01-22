package com.vb2.httpserver;

import com.vb2.httpserver.config.ConfigurationManager;
import com.vb2.httpserver.core.ServerListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    public static void main(String[] args) {
        LOGGER.info("Server starting...");

        var configurationManager = ConfigurationManager.getInstance();
        configurationManager.loadConfigurationFile("src/main/resources/http.json");
        var conf = configurationManager.getCurrentConfiguration();


        LOGGER.info("Using port: {}", conf.getPort());
        LOGGER.info("Using webroot: {}", conf.getWebroot());

        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(conf.getPort(), conf.getWebroot());
            serverListenerThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
