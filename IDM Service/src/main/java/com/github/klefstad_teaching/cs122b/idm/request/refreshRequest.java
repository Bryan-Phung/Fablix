package com.github.klefstad_teaching.cs122b.idm.request;

public class refreshRequest {

    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public refreshRequest setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
