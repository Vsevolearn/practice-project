package com.uclab.everytree.models.serializers.spinner;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public class SiteType {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("name_ru")
    private String name_ru;

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        if (Locale.getDefault().getLanguage().equals("en")) {
            return this.name;
        }
        else
        {
            return this.name_ru;
        }
    }
}
