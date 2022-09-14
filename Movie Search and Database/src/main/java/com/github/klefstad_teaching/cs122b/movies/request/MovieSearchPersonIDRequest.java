package com.github.klefstad_teaching.cs122b.movies.request;

public class MovieSearchPersonIDRequest {
    private Long personID;
    private Integer limit;
    private Integer page;
    private String orderBy;
    private String direction;

    public Long getPersonID() {
        return personID;
    }

    public MovieSearchPersonIDRequest setPersonID(Long personID) {
        this.personID = personID;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public MovieSearchPersonIDRequest setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public MovieSearchPersonIDRequest setPage(Integer page) {
        this.page = page;
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public MovieSearchPersonIDRequest setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public String getDirection() {
        return direction;
    }

    public MovieSearchPersonIDRequest setDirection(String direction) {
        this.direction = direction;
        return this;
    }
}
