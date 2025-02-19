package com.vb2.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Supported versions of http enumeration
 */
public enum HttpVersion {
    HTTP_1_1("HTTP/1.1", 1, 1);

    public final String LITERAL;
    public final int MAJOR;
    public final int MINOR;
    private static final Pattern httpVersionRegexPattern = Pattern.compile("HTTP/(?<major>\\d+)\\.(?<minor>\\d+)$");

    HttpVersion(String LITERAL, int MAJOR, int MINOR) {
        this.LITERAL = LITERAL;
        this.MAJOR = MAJOR;
        this.MINOR = MINOR;
    }

    public static HttpVersion getBestCompatibleVersion(String literalVersion) throws BadHttpVersionException {
        Matcher matcher = httpVersionRegexPattern.matcher(literalVersion);
        if (!matcher.find() || matcher.groupCount() != 2) {
            throw new BadHttpVersionException();
        }
        int major = Integer.parseInt(matcher.group("major"));
        int minor = Integer.parseInt(matcher.group("minor"));

        HttpVersion tempBestCompatibleVersion = null;
        for (HttpVersion version : HttpVersion.values()) {
            if (version.LITERAL.equals(literalVersion)) {
                return version;
            } else {
                if (version.MAJOR == major) {
                    if (version.MINOR < minor) {
                        tempBestCompatibleVersion = version;
                    }
                }
            }
        }
        return tempBestCompatibleVersion;
    }



}
