package com.vb2.httpserver.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.vb2.httpserver.util.Json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ConfigurationManager {
    public static ConfigurationManager myConfigurationManager;
    private static Configuration myCurrentConfiguration;

    private ConfigurationManager() {

    }

    public static ConfigurationManager getInstance() {
        if (myConfigurationManager == null) {
            myConfigurationManager = new ConfigurationManager();
        }
        return myConfigurationManager;
    }

    /**
     * Used to load a configuration file by the path provided
     */
    public void loadConfigurationFile(String filePath) {
        StringBuffer sb = new StringBuffer();

        try (FileReader fileReader = new FileReader(filePath)) { // try-with-resources
            int i;
            while ((i = fileReader.read()) != -1) {
                sb.append((char) i);
            }
        } catch (FileNotFoundException e) {
            throw new HttpConfigurationException("File not found: " + filePath);
        } catch (IOException e) {
            throw new HttpConfigurationException(e);
        }

        JsonNode conf;
        try {
            conf = Json.parse(sb.toString());
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Error parsing the Configuration file: ", e);
        }
        try {
            myCurrentConfiguration = Json.fromJson(conf, Configuration.class);
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Error parsing the Configuration file, internal: ", e);
        }
    }

    /**
     * Returns the current loaded configuration
     */
    public Configuration getCurrentConfiguration() {
        if (myCurrentConfiguration == null) {
            throw new HttpConfigurationException("No Current Configuration Set.");
        }
        return myCurrentConfiguration;
    }
}
