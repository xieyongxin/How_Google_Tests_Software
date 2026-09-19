package io.github.xieyongxin.testing.foundations;

import java.net.URI;

/** Small, deterministic URL policy used by the concept prototype. */
public final class UrlValidator {

    /** Returns whether the value is an absolute HTTP or HTTPS URL with a host. */
    public boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            URI uri = URI.create(value.trim());
            String scheme = uri.getScheme();
            return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && uri.getHost() != null
                    && !uri.getHost().isBlank();
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}

