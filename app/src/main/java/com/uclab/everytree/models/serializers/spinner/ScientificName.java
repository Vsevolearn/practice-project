package com.uclab.everytree.models.serializers.spinner;

import com.google.gson.annotations.SerializedName;

public class ScientificName {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
