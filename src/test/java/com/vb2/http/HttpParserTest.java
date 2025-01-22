package com.vb2.http;

import com.vb2.http.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpParserTest {
    private HttpParser httpParser;

    @BeforeAll
    public void beforeMethod() {
        httpParser = new HttpParser();
    }

    @Test
    void parseHttpRequest() {

        HttpRequest actualRequest = null;
        try {
            actualRequest = httpParser.parseHttpRequest(generateValidGETRequestTestCase());
        } catch (HttpParsingException e) {
            Assertions.fail();
        }

        Assertions.assertNotNull(actualRequest);
        Assertions.assertEquals(HttpMethod.GET, actualRequest.getMethod());
        Assertions.assertEquals("/", actualRequest.getRequestTarget());
        Assertions.assertEquals(HttpVersion.HTTP_1_1, actualRequest.getBestCompatibleHttpVersion());
    }

    @Test
    void parseHttpRequestInvalidMethodName() {
        HttpParsingException ex = Assertions.assertThrows(HttpParsingException.class, () ->
                httpParser.parseHttpRequest(generateRequestLineInvalidMethodName())
        );

        Assertions.assertEquals(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED, ex.getErrorCode());
    }

    @Test
    void parseHttpRequestTooManyElements() {
        HttpParsingException ex = Assertions.assertThrows(HttpParsingException.class, () ->
                httpParser.parseHttpRequest(generateRequestLineTooManyLineElements())
        );

        Assertions.assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, ex.getErrorCode());
    }

    @Test
    void parseHttpRequestLineEmpty() {
        HttpParsingException ex = Assertions.assertThrows(HttpParsingException.class, () ->
                httpParser.parseHttpRequest(generateRequestLineEmptyFirstLine())
        );

        Assertions.assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, ex.getErrorCode());
    }

    @Test
    void parseHttpRequestLineNotEnoughElements() {
        HttpParsingException ex = Assertions.assertThrows(HttpParsingException.class, () ->
                httpParser.parseHttpRequest(generateRequestLineNotEnoughElements())
        );

        Assertions.assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, ex.getErrorCode());
    }

    @Test
    void parseHttpRequestLineBadHttpVersion() {
        HttpParsingException ex = Assertions.assertThrows(HttpParsingException.class, () ->
                httpParser.parseHttpRequest(generateRequestBadHttpVersion())
        );

        Assertions.assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, ex.getErrorCode());
    }
    @Test
    void parseHttpRequestLineUnsupportedHttpVersion() {
        HttpParsingException ex = Assertions.assertThrows(HttpParsingException.class, () ->
                httpParser.parseHttpRequest(generateRequestUnsupportedHttpVersion())
        );

        Assertions.assertEquals(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED, ex.getErrorCode());
    }

    @Test
    void parseHttpRequestLineSupportedHttpVersion() {
        try {
            HttpRequest httpRequest = httpParser.parseHttpRequest(
                    generateRequestSupportedHttpVersion());
            Assertions.assertNotNull(httpRequest);
            Assertions.assertEquals(HttpVersion.HTTP_1_1, httpRequest.getBestCompatibleHttpVersion());
            Assertions.assertEquals("HTTP/1.2", httpRequest.getOriginalHttpVersion());
        } catch (HttpParsingException e) {
            Assertions.fail();
        }



    }

    private InputStream generateValidGETRequestTestCase() {
        String rawData = "GET / HTTP/1.1\n\r" +
                "Host: localhost:8080\n\r" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:134.0) Gecko/20100101 Firefox/134.0\n\r\r" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n\r" +
                "Accept-Language: uk,en-US;q=0.7,en;q=0.3\n\r" +
                "Accept-Encoding: gzip, deflate, br, zstd\n\r" +
                "Connection: keep-alive\n\r" +
                "Upgrade-Insecure-Requests: 1\n\r" +
                "Sec-Fetch-Dest: document\n\r" +
                "Sec-Fetch-Mode: navigate\n\r" +
                "Sec-Fetch-Site: none\n\r" +
                "Sec-Fetch-User: ?1\n\r" +
                "Priority: u=0, i\n\r";

        return toInputStream(rawData);
    }

    private InputStream generateRequestLineInvalidMethodName() {
        return toInputStream("GeT / HTTP/1.1\n");
    }

    private InputStream generateRequestLineTooManyLineElements() {
        return toInputStream("GETTING /SOURCE1 /SOURCE2 HTTP/1.1\n");
    }

    private InputStream generateRequestLineEmptyFirstLine() {
        return toInputStream("Host: localhost:8080\n");
    }

    private InputStream generateRequestLineNotEnoughElements() {
        return toInputStream("GET http:/example.com/ \n");
    }

    private InputStream generateRequestBadHttpVersion() {
        return toInputStream("GET / HTTP/1.1.1  \n");
    }

    private InputStream generateRequestUnsupportedHttpVersion() {
        return toInputStream("GET / HTTP/2.1  \n");
    }

    private InputStream generateRequestSupportedHttpVersion() {
        return toInputStream("GET / HTTP/1.2  \n");
    }

    private InputStream toInputStream(String str) {
        return new ByteArrayInputStream(
                str.getBytes(StandardCharsets.UTF_8)
        );
    }

}

