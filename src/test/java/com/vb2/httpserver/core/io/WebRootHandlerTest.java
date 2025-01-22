package com.vb2.httpserver.core.io;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WebRootHandlerTest {

    private WebRootHandler webRootHandler;

    private Method checkIfEndsWithSlashMethod;

    private Method checkIfProvidedRelativePathExistsMethod;

    @BeforeAll
    public void beforeClass() throws WebRootNotFoundException, NoSuchMethodException {
        webRootHandler = new WebRootHandler("WebRoot");
        Class<WebRootHandler> cls = WebRootHandler.class;
        checkIfEndsWithSlashMethod = cls.getDeclaredMethod("checkIfEndsWithSlash", String.class);
        checkIfEndsWithSlashMethod.setAccessible(true);

        checkIfProvidedRelativePathExistsMethod = cls.getDeclaredMethod("checkIfProvidedRelativePathExists", String.class);
        checkIfProvidedRelativePathExistsMethod.setAccessible(true);
    }

    @Test
    void constructorGoodPath() {
        assertDoesNotThrow(() -> new WebRootHandler("D:\\Programming\\Java\\pet-projects\\MyHttpServer\\WebRoot"));
    }

    @Test
    void constructorBadPath() {
        assertThrows(WebRootNotFoundException.class, () -> new WebRootHandler("D:\\Programming\\Java\\pet-projects\\MyHttpServer\\WebRoot1 "));
    }

    @Test
    void constructorGoodRelativePath() {
        assertDoesNotThrow(() -> new WebRootHandler("WebRoot"));
    }

    @Test
    void constructorBadRelativePath() {
        assertThrows(WebRootNotFoundException.class,() -> new WebRootHandler("WebRoot2"));
    }

    @Test
    void checkIfEndsWithSlashMethodFalse() {
        try {
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(webRootHandler,"index.html");
            assertFalse(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    void checkIfEndsWithSlashMethodFalse2() {
        try {
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(webRootHandler,"/index.html");
            assertFalse(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    void checkIfEndsWithSlashMethodFalse3() {
        try {
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(webRootHandler,"/private/index.html");
            assertFalse(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    void checkIfEndsWithSlashMethodTrue() {
        try {
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(webRootHandler,"/");
            assertTrue(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    void checkIfEndsWithSlashMethodTrue2() {
        try {
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(webRootHandler,"/private/");
            assertTrue(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    void testWebRootFilePathExists() {
        try {
            boolean result = (Boolean) checkIfProvidedRelativePathExistsMethod.invoke(webRootHandler, "/index.html");
            assertTrue(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    void testWebRootFilePathExistsGoodRelative() {
        try {
            boolean result = (Boolean) checkIfProvidedRelativePathExistsMethod.invoke(webRootHandler, "/./././index.html");
            assertTrue(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    void testWebRootFilePathExistsDoesNotExist() {
        try {
            boolean result = (Boolean) checkIfProvidedRelativePathExistsMethod.invoke(webRootHandler, "/indexNotHere.html");
            assertFalse(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    void testWebRootFilePathExistsInvalid() {
        try {
            boolean result = (Boolean) checkIfProvidedRelativePathExistsMethod.invoke(webRootHandler, "/./ABC");
            assertFalse(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail(e);
        }
    }


    @Test
    void testGetFileMimeTypeText() {
        try {
            String mimeType = webRootHandler.getFileMimeType("/");
            assertEquals("text/html", mimeType);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void testGetFileMimeTypeJpg() {
        try {
            String mimeType = webRootHandler.getFileMimeType("/image.jpg");
            assertEquals("image/jpeg", mimeType);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void testGetFileMimeTypeDefault() {
        try {
            String mimeType = webRootHandler.getFileMimeType("/abc.xyz");
            assertEquals("application/octet-stream", mimeType);
        } catch (Exception e) {
            fail(e);
        }
    }

 }