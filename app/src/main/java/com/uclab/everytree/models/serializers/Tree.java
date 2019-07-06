package com.uclab.everytree.models.serializers;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Tree {
	@SerializedName("id")
	private int id;

	@SerializedName("location")
	private LatLng location;

	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return this.id;
	}

	public LatLng getLocation()
	{
		return this.location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}
}