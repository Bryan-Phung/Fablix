package com.github.klefstad_teaching.cs122b.movies.config;

public class DetailMovie{
        private Long id;
        private String title;
        private Integer year;
        private String director;
        private Double rating;
        private Long numVotes;
        private Long budget;
        private Long revenue;
        private String overview;
        private String backdropPath;
        private String posterPath;
        private Boolean hidden;

        public Long getNumVotes() {
                return numVotes;
        }

        public DetailMovie setNumVotes(Long numVotes) {
                this.numVotes = numVotes;
                return this;
        }

        public Long getId() {
                return id;
        }

        public DetailMovie setId(Long id) {
                this.id = id;
                return this;
        }

        public String getTitle() {
                return title;
        }

        public DetailMovie setTitle(String title) {
                this.title = title;
                return this;
        }

        public Integer getYear() {
                return year;
        }

        public DetailMovie setYear(Integer year) {
                this.year = year;
                return this;
        }

        public String getDirector() {
                return director;
        }

        public DetailMovie setDirector(String director) {
                this.director = director;
                return this;
        }

        public Double getRating() {
                return rating;
        }

        public DetailMovie setRating(Double rating) {
                this.rating = rating;
                return this;
        }

        public Long getBudget() {
                return budget;
        }

        public DetailMovie setBudget(Long budget) {
                this.budget = budget;
                return this;
        }

        public Long getRevenue() {
                return revenue;
        }

        public DetailMovie setRevenue(Long revenue) {
                this.revenue = revenue;
                return this;
        }

        public String getOverview() {
                return overview;
        }

        public DetailMovie setOverview(String overview) {
                this.overview = overview;
                return this;
        }

        public String getBackdropPath() {
                return backdropPath;
        }

        public DetailMovie setBackdropPath(String backdropPath) {
                this.backdropPath = backdropPath;
                return this;
        }

        public String getPosterPath() {
                return posterPath;
        }

        public DetailMovie setPosterPath(String posterPath) {
                this.posterPath = posterPath;
                return this;
        }

        public Boolean getHidden() {
                return hidden;
        }

        public DetailMovie setHidden(Boolean hidden) {
                this.hidden = hidden;
                return this;
        }
}
