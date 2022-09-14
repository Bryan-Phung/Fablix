package com.github.klefstad_teaching.cs122b.movies.model;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.config.*;

import java.util.List;

public class GetMovieByIDResponse extends ResponseModel<GetMovieByIDResponse> {
    private DetailMovie movie;
    private List<GenericInformation> genres;
    private List<GenericInformation> persons;

    public DetailMovie getMovie() {
        return movie;
    }

    public GetMovieByIDResponse setMovie(DetailMovie movie) {
        this.movie = movie;
        return this;
    }

    public List<GenericInformation> getGenres() {
        return genres;
    }

    public GetMovieByIDResponse setGenres(List<GenericInformation> genres) {
        this.genres = genres;
        return this;
    }

    public List<GenericInformation> getPersons() {
        return persons;
    }

    public GetMovieByIDResponse setPersons(List<GenericInformation> persons) {
        this.persons = persons;
        return this;
    }
}
