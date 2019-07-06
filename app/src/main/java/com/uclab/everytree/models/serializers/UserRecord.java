package com.uclab.everytree.models.serializers;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserRecord {
    @SerializedName("user__username")
    private String username;

    @SerializedName("date_input")
    private Date date_input;

    public String getUsername() {
        return this.username;
    }

    public Date getDateInput() {
        return this.date_input;
    }
}
