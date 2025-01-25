package com.vb2.httpserver.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;

public class WebRootHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebRootHandler.class);
    private File webRoot;

    public WebRootHandler(String webRootPath) throws WebRootNotFoundException {
        this.webRoot = new File(webRootPath);
        LOGGER.info("WebRootHandler initialized with path: " + webRootPath);
        if (!webRoot.exists() || !webRoot.isDirectory()) {
            throw new WebRootNotFoundException("Webroot provided does not exist or is not a folder");
        }
    }

    private boolean checkIfEndsWithSlash(String relativePath) {
        return relativePath.endsWith("/");
    }

    private boolean checkIfProvidedRelativePathExists(String relativePath) {
        File file = new File(webRoot, relativePath);
        if (!file.exists()) {
            return false;
        }

        try {
            if (file.getCanonicalPath().startsWith(webRoot.getCanonicalPath())) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    public byte[] getFileBytes(String relativePath) throws IOException {
        if (relativePath.endsWith("/")) {
            relativePath += "index.html";
        }

        if (!checkIfProvidedRelativePathExists(relativePath)) {
            throw new FileNotFoundException("File not found: " + relativePath);
        }

        File file = new File(this.webRoot, relativePath);
        return Files.readAllBytes(file.toPath());
    }

    public String getFileMimeType(String relativePath) throws FileNotFoundException {
        if (checkIfEndsWithSlash(relativePath)) {
            relativePath += "index.html"; // By default, serve the index.html, if it exists.
        }

        if (!checkIfProvidedRelativePathExists(relativePath)) {
            throw new FileNotFoundException("File not found: " + relativePath);
        }

        File file = new File(this.webRoot, relativePath);

        String mimeType = URLConnection.getFileNameMap().getContentTypeFor(file.getName());

        if (mimeType == null) {
            // RFC 7231 Section 4.3.3 - If no Content-Type header is present, the default is application/octet-stream
            return "application/octet-stream";
        }

        return mimeType;
    }

}
