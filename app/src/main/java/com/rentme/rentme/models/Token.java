package com.rentme.rentme.models;

public class Token {
    private String token_type;
    private String access_token;

    public String getToken_type() {
        return token_type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
