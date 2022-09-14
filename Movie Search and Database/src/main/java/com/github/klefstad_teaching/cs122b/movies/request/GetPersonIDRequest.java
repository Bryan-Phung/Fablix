package com.github.klefstad_teaching.cs122b.movies.request;

public class GetPersonIDRequest {
    private Long personId;

    public Long getPersonId() {
        return personId;
    }

    public GetPersonIDRequest setPersonId(Long personId) {
        this.personId = personId;
        return this;
    }
}
