package io.github.xieyongxin.testing.foundations;

/** Disposable concept-stage application for validating the AddUrl idea. */
public final class AddUrlPrototype {

    private final UrlValidator validator;
    private final InMemoryIndex index;

    public AddUrlPrototype(UrlValidator validator, InMemoryIndex index) {
        this.validator = validator;
        this.index = index;
    }

    /** Validates and stores a URL without introducing a network or RPC dependency. */
    public AddUrlResult validateAndAdd(String rawUrl, String comment) {
        if (!validator.isValid(rawUrl)) {
            return new AddUrlResult(false, null, "URL必须是带主机的HTTP或HTTPS地址");
        }
        String normalized = rawUrl.trim();
        index.add(normalized, comment);
        return new AddUrlResult(true, normalized, "");
    }

    public InMemoryIndex index() {
        return index;
    }

    /** Runs the smallest useful demonstration of the concept. */
    public static void main(String[] args) {
        AddUrlPrototype prototype = new AddUrlPrototype(new UrlValidator(), new InMemoryIndex());
        AddUrlResult result = prototype.validateAndAdd("https://www.example.com", "demo");
        System.out.println("accepted=" + result.accepted());
        System.out.println("url=" + result.normalizedUrl());
        System.out.println("indexed=" + prototype.index().contains("https://www.example.com"));
    }
}

