package com.vb2.http;

import com.vb2.http.HttpParser;
import com.vb2.http.HttpRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpHeadersParseTest {
    private HttpParser httpParser;
    private Method parseHeadersMethod;

    @BeforeAll
    public void beforeMethod() throws NoSuchMethodException {
        httpParser = new HttpParser();
        Class<HttpParser> cls = HttpParser.class;
        parseHeadersMethod = cls.getDeclaredMethod("parseHeaders", InputStreamReader.class, HttpRequest.class);
        parseHeadersMethod.setAccessible(true);
    }

    @Test
    public void testSimpleSingleHeader() throws InvocationTargetException, IllegalAccessException {
        HttpRequest request = new HttpRequest();
        parseHeadersMethod.invoke(
                httpParser,
                generateSimpleSingleHeaderMessage(),
                request);
        Assertions.assertEquals(1, request.getHeaderNames().size());
        Assertions.assertEquals("localhost:8080", request.getHeaders().get("Host"));
    }

    @Test
    public void testMultipleHeaders() throws InvocationTargetException, IllegalAccessException {
        HttpRequest request = new HttpRequest();
        parseHeadersMethod.invoke(
                httpParser,
                generateMultipleHeadersMessage(),
                request);

        System.out.println(request.printHeadersPretty());

        Assertions.assertEquals(12, request.getHeaderNames().size());
        Assertions.assertEquals("localhost:8080", request.getHeaderName("Host"));
    }


    private InputStreamReader generateSimpleSingleHeaderMessage() {
        String rawData = "Host: localhost:8080\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(StandardCharsets.UTF_8)
        );

        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return reader;
    }

    private InputStreamReader generateMultipleHeadersMessage() {
        String rawData = "Host: localhost:8080\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:134.0) Gecko/20100101 Firefox/134.0\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" +
                "Accept-Language: uk,en-US;q=0.7,en;q=0.3\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\n" +
                "Connection: keep-alive\n" +
                "Upgrade-Insecure-Requests: 1\n" +
                "Sec-Fetch-Dest: document\n" +
                "Sec-Fetch-Mode: navigate\n" +
                "Sec-Fetch-Site: none\n" +
                "Sec-Fetch-User: ?1\n" +
                "Priority: u=0, i\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(StandardCharsets.UTF_8)
        );
        
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return reader;
    }
}
