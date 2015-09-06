package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class RuntimeFieldDoc implements Serializable {
    private static final long serialVersionUID = 1;

    private final String qualifiedName;
    private final String comment;

    public RuntimeFieldDoc(@JsonProperty("qualifiedName") String qualifiedName,
                           @JsonProperty("comment") String comment) {
        this.qualifiedName = qualifiedName;
        this.comment = comment;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getComment() {
        return comment;
    }
}