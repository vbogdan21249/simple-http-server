package com.vb2.httpserver;

import com.vb2.httpserver.config.Configuration;
import com.vb2.httpserver.config.ConfigurationManager;

public class HttpServer {
    public static void main(String[] args) {
        System.out.println("Server starting...");
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();
        System.out.println("Using port: " + conf.getPort());
        System.out.println("Using webroot: " + conf.getWebroot());
    }
}
