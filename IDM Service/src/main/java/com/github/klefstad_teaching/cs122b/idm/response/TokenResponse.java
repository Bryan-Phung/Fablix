package com.github.klefstad_teaching.cs122b.idm.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

public class TokenResponse extends ResponseModel<TokenResponse> {

    private String accessToken;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public TokenResponse setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public TokenResponse setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
