package com.github.klefstad_teaching.cs122b.movies.config;

public class GenericInformation {

    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public GenericInformation setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public GenericInformation setName(String name) {
        this.name = name;
        return this;
    }
}
