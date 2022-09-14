package com.github.klefstad_teaching.cs122b.movies.request;

public class getMovieIDRequest {
    private Long movieID;

    public Long getMovieID() {
        return movieID;
    }

    public getMovieIDRequest setMovieID(Long movieID) {
        this.movieID = movieID;
        return this;
    }
}
