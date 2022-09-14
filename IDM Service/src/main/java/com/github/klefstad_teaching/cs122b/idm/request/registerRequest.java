package com.github.klefstad_teaching.cs122b.idm.request;

public class registerRequest {
    private String email;
    private char[] password;

    public String getEmail() {
        return email;
    }

    public registerRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public char[] getPassword() {
        return password;
    }

    public registerRequest setPassword(char[] password) {
        this.password = password;
        return this;
    }
}
