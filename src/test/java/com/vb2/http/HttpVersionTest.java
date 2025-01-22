package com.vb2.http;

import com.vb2.http.BadHttpVersionException;
import com.vb2.http.HttpVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HttpVersionTest {

    @Test
    void getBestCompatibleVersionExactMatch() {
        HttpVersion version = null;
        try {
            version = HttpVersion.getBestCompatibleVersion("HTTP/1.1");
        } catch (BadHttpVersionException e) {
            Assertions.fail();
        }

        Assertions.assertNotNull(version);
        Assertions.assertEquals(version, HttpVersion.HTTP_1_1);
    }

    @Test
    void getBestCompatibleVersionBadFormat() {
        Assertions.assertThrows(BadHttpVersionException.class, () ->
                HttpVersion.getBestCompatibleVersion("HTTP/1.1.1"));
    }
}
