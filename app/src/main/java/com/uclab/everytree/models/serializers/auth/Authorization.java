package com.uclab.everytree.models.serializers.auth;

import com.google.gson.annotations.SerializedName;

public class Authorization {
    @SerializedName("access")
    private String accessToken;

    @SerializedName("refresh")
    private String refreshToken;

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setRefreshToken(String token) {
        this.refreshToken = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
