package com.vb2.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpRequest extends HttpMessage {
    private HttpMethod method;
    private String requestTarget;
    private String originalHttpVersion; // literal from the request
    private HttpVersion bestCompatibleHttpVersion;
    private Map<String, String> headers = new HashMap<>();

    HttpRequest() {

    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getRequestTarget() {
        return this.requestTarget;
    }

    public void setRequestTarget(String requestTarget) {
        this.requestTarget = requestTarget;
    }

    public String getOriginalHttpVersion() {
        return this.originalHttpVersion;
    }

    public HttpVersion getBestCompatibleHttpVersion() {
        return this.bestCompatibleHttpVersion;
    }

    public void setHttpVersion(String originalHttpVersion) throws BadHttpVersionException, HttpParsingException {
        this.originalHttpVersion = originalHttpVersion;
        this.bestCompatibleHttpVersion = HttpVersion.getBestCompatibleVersion(originalHttpVersion);
        if (this.bestCompatibleHttpVersion == null) {
            throw new HttpParsingException(
                    HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED
            );
        }
    }

    public void addHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    public String getHeaderName(String headerName) {
        return headers.get(headerName);
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String printHeadersPretty() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HttpRequest request = (HttpRequest) obj;
        return method == request.method && requestTarget.equals(request.requestTarget) && originalHttpVersion.equals(request.originalHttpVersion);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method.name() +
                ", requestTarget='" + requestTarget + '\'' +
                ", httpVersion='" + originalHttpVersion + '\'' +
                '}';
    }

}
