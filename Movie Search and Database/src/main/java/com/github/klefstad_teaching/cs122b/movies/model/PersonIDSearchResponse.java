package com.github.klefstad_teaching.cs122b.movies.model;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.config.Person;

public class PersonIDSearchResponse extends ResponseModel<PersonIDSearchResponse> {
    private Person person;

    public Person getPerson() {
        return person;
    }

    public PersonIDSearchResponse setPerson(Person person) {
        this.person = person;
        return this;
    }
}
