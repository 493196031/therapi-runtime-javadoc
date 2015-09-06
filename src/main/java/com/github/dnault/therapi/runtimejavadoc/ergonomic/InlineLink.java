package com.github.dnault.therapi.runtimejavadoc.ergonomic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InlineLink extends CommentElement {
    private final Link link;

    public InlineLink(@JsonProperty("link") Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }
}