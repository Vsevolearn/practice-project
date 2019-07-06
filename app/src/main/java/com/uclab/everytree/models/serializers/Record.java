package com.uclab.everytree.models.serializers;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Record {
    @SerializedName("trunk_diameter")
    private double trunk_diameter;
    @SerializedName("height")
    private double height;
    @SerializedName("skeletal_branches_number")
    private int skeletal_branches_number;
    @SerializedName("condition")
    private String condition;
    @SerializedName("nearest_address")
    private String nearest_address;
    @SerializedName("date_input")
    private Date date_input;
    @SerializedName("date_planted")
    private Date date_planted;
    @SerializedName("date_removed")
    private Date date_removed;
    @SerializedName("common_name")
    private Integer common_name;
    @SerializedName("site_type")
    private Integer site_type;
    @SerializedName("tree")
    private Integer tree_id;
    @SerializedName("user")
    private String user;

    public String getUser() {
        return this.user;
    }

    public Integer getTreeId() {
        return this.tree_id;
    }

    public void setTreeId(Integer tree_id) {
        this.tree_id = tree_id;
    }

    public void setCommonName(Integer common_name) {
        this.common_name = common_name;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setDatePlanted(Date date_planted) {
        this.date_planted = date_planted;
    }

    public void setDateInput(Date date_input) {
        this.date_input = date_input;
    }

    public void setDateRemoved(Date date_removed) {
        this.date_removed = date_removed;
    }

    public void setNearestAddress(String nearest_address) {
        this.nearest_address = nearest_address;
    }

    public void setSiteType(Integer site_type) {
        this.site_type = site_type ;
    }

    public void setSkeletalBranchesNumber(int skeletal_branches_number) {
        this.skeletal_branches_number = skeletal_branches_number;
    }

    public void setTrunkDiameter(double trunk_diameter) {
        this.trunk_diameter = trunk_diameter;
    }

    public Integer getCommonName()
    {
        return this.common_name;
    }

    public String getNearestAddress()
    {
        return this.nearest_address;
    }

    public double getTrunkDiameter() {
        return this.trunk_diameter;
    }

    public int getSkeletalBranchesNumber() {
        return this.skeletal_branches_number;
    }

    public double getHeight() {
        return this.height;
    }

    public Date getDateInput() {
        return this.date_input;
    }

    public Date getDatePlanted() {
        return this.date_planted;
    }

    public Date getDateRemoved() {
        return this.date_removed;
    }

    public String getCondition() {
        return this.condition;
    }

    public Integer getSiteType() {
        return this.site_type;
    }
}