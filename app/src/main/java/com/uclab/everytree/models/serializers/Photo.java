package com.uclab.everytree.models.serializers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Photo implements Parcelable{
    @SerializedName("id")
    private Integer id;

    @SerializedName("url")
    private String mUrl;

    public Integer getId() {
        return this.id;
    }

    public String getUrl()
    {
        return this.mUrl;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public Photo(String url) {
        this.mUrl = url;
    }

    protected Photo(Parcel in) {
        this.mUrl = in.readString();
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mUrl);
    }
}
