package com.uclab.everytree.models.serializers;

import com.google.gson.annotations.SerializedName;

public class UsersScores {
    @SerializedName("user__username")
    private String username;

    @SerializedName("total_records")
    private Integer total_records;

    public Integer getTotalRecords() {
        return this.total_records;
    }

    public String getUsername() {
        return this.username;
    }
}
