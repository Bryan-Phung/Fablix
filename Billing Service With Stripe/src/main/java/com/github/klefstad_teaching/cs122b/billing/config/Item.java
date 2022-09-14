package com.github.klefstad_teaching.cs122b.billing.config;

import java.math.BigDecimal;

public class Item {

    private BigDecimal unitPrice;
    private Integer quantity;
    private Long movieId;
    private String movieTitle;
    private String backdropPath;
    private String posterPath;

    public Long getMovieId() {
        return movieId;
    }

    public Item setMovieId(Long id) {
        this.movieId = id;
        return this;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Item setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Item setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public Item setMovieTitle(String title) {
        this.movieTitle = title;
        return this;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Item setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Item setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }
}
