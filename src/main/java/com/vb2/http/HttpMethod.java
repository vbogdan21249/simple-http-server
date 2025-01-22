package com.vb2.http;

public enum HttpMethod {
    // RFC 7231, section 4
    // Method token is case-sensitive
    // 
    // All general-purpose servers MUST support the methods GET and HEAD.
    // All other methods are optional.
    GET, HEAD;
    public static final int MAX_LENGTH;
    static {
        int tempMaxLength = -1;
        for (HttpMethod method : values()) {
            if (tempMaxLength < method.name().length()) {
                tempMaxLength = method.name().length();
            }
        }
        MAX_LENGTH = tempMaxLength;
    }
}
