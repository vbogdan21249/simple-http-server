package com.vb2.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

    public HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        HttpRequest request = new HttpRequest();
        try {
            parseRequestLine(reader, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            parseHeaders(reader, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        parseBody(reader, request);

        return request;
    }

    private void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {

        BufferedReader bufferedReader = new BufferedReader(reader);
        String requestLine = bufferedReader.readLine();

        String[] lineElements = requestLine.split(" ");
        if (lineElements.length != 3) {
            LOGGER.warn("Invalid request line: [{}]", requestLine);
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        boolean methodSet = false;
        String requestedMethodName = lineElements[0];
        for (HttpMethod possibleMethod : HttpMethod.values()) {
            if (requestedMethodName.equals(possibleMethod.name())) {
                request.setMethod(possibleMethod);
                methodSet = true;
                break;
            }
        }
        if (!methodSet) {
            LOGGER.warn("Invalid method provided: [{}]", requestedMethodName);
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
        }

        request.setRequestTarget(lineElements[1]);

        try {
            request.setHttpVersion(lineElements[2]);
        } catch (BadHttpVersionException e) {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    private void parseHeaders(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        Map<String, String> headers = new HashMap<>();

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(':');
            if (colonIndex == -1) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }

            String headerName = line.substring(0, colonIndex).trim();
            String headerValue = line.substring(colonIndex + 1).trim();

            if (headerName.isEmpty() || headerValue.isEmpty()) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }

            headers.put(headerName, headerValue);
        }

        request.setHeaders(headers);
    }

    private void parseBody(InputStreamReader reader, HttpRequest request) {
    }


}
