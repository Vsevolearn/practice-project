package com.uclab.everytree.models.serializers;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public class UserScore {
    @SerializedName("common_name__name")
    private String common_name;

    @SerializedName("common_name__name_ru")
    private String common_name_ru;

    @SerializedName("total_records")
    private Integer total_records;

    public Integer getTotalRecords() {
        return this.total_records;
    }

    public String getName() {
        if (Locale.getDefault().getLanguage().equals("en")) {
            return this.common_name;
        }
        else
        {
            return this.common_name_ru;
        }
    }
}
