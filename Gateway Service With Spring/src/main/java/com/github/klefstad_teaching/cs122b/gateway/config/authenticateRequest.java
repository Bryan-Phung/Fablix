package com.github.klefstad_teaching.cs122b.gateway.config;

public class authenticateRequest {

    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public authenticateRequest setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
