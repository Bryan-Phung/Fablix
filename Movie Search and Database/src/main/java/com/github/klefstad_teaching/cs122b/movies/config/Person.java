package com.github.klefstad_teaching.cs122b.movies.config;

public class Person {

    private Long id;
    private String name;
    private String birthday;
    private String biography;
    private String birthplace;
    private Float popularity;
    private String profilePath;

    public String getBiography() {
        return biography;
    }

    public Person setBiography(String biography) {
        if (biography != null) {
            this.biography = biography.replace("\r","");
        }
        else
        {
            this.biography = null;
        }
        return this;
    }

    public Long getId() {
        return id;
    }

    public Person setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public Person setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public Person setBirthplace(String birthplace) {
        this.birthplace = birthplace;
        return this;
    }

    public Float getPopularity() {
        return popularity;
    }

    public Person setPopularity(Float popularity) {
        this.popularity = popularity;
        return this;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public Person setProfilePath(String profilePath) {
        this.profilePath = profilePath;
        return this;
    }
}
