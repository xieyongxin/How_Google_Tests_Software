package io.github.xieyongxin.testing.foundations;

/** Result returned by the concept prototype. */
public record AddUrlResult(boolean accepted, String normalizedUrl, String reason) {}

