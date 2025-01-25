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
        LOGGER.info(" * Parsing HTTP request started.");
        InputStreamReader reader = new InputStreamReader(inputStream);

        HttpRequest request = new HttpRequest();
        try {
            parseRequestLine(reader, request);
        } catch (IOException e) {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        try {
            parseHeaders(reader, request);
        } catch (IOException e) {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        parseBody(reader, request);
        LOGGER.info(" * Parsing HTTP request finished.");
        return request;
    }
    public String getRawRequest(InputStream inputStream) throws IOException {
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader request = new BufferedReader(in);

        int i;
        StringBuffer sb = new StringBuffer();
        while ((i = request.read()) != -1) {
            sb.append((char) i);
        }

        return sb.toString();
    }

    private void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        BufferedReader in = new BufferedReader(reader);
        String requestLine = in.readLine();

        String[] lineElements = requestLine.split(" ");
        if (lineElements.length != 3) {
            LOGGER.error("Invalid request line: [{}]", requestLine);
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
            LOGGER.error("Invalid HTTP version: [{}]", lineElements[2]);
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
                LOGGER.error("No colon found in header: [{}]", line);
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
