package com.uclab.everytree.models.serializers.spinner;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public class CommonName {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("name_ru")
    private String name_ru;

    @SerializedName("scientific_name")
    private Integer scientific_name_id;

    public Integer getId() {
        return this.id;
    }

    public Integer getScientificNameId() {
        return this.scientific_name_id;
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
