package com.uclab.everytree.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uclab.everytree.models.serializers.Photo;
import com.uclab.everytree.models.serializers.Record;
import com.uclab.everytree.models.serializers.Tree;
import com.uclab.everytree.models.serializers.spinner.CommonName;
import com.uclab.everytree.models.serializers.spinner.ScientificName;
import com.uclab.everytree.models.serializers.spinner.SiteType;

import java.util.List;

public class AppService {
    private SharedPreferences prefs;
    private Gson gson = new Gson();
    private static Record record;

    public AppService(Context _cxt) {
        prefs = PreferenceManager.getDefaultSharedPreferences(_cxt);
    }

    public static Record getEditableRecord()
    {
        if (record == null)
        {
            record = new Record();
        }

        return record;
    }

    public void setRecord(Record _record)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Record", gson.toJson(_record));
        editor.apply();
    }

    public void setTree(Tree tree)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Tree", gson.toJson(tree));
        editor.apply();
    }

    public Tree getTree()
    {
        return gson.fromJson(prefs.getString("Tree", null), Tree.class);
    }

    public Record getRecord()
    {
        return gson.fromJson(prefs.getString("Record", null), Record.class);
    }

    public void setPhotosToUpload(List<Photo> images)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Photos", gson.toJson(images));
        editor.apply();
    }

    public List<Photo> getPhotosToUpload()
    {
        return gson.fromJson(prefs.getString("Photos", null), new TypeToken<List<Photo>>(){}.getType());
    }

    public void ClearAfterWrite()
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("Photos");
        record = null;
        editor.apply();
    }

    public void ClearAll()
    {
        SharedPreferences.Editor editor = prefs.edit();
        record = null;
        editor.remove("Record");
        editor.remove("Tree");
        editor.remove("Photos");
        editor.remove("isAddMode");
        editor.remove("CommonNames");
        editor.remove("ScientificNames");
        editor.remove("SiteTypes");
        editor.apply();
    }

    public void setCommonNames(List<CommonName> names)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CommonNames", gson.toJson(names));
        editor.apply();
    }

    public List<CommonName> getCommonNames()
    {
        return gson.fromJson(prefs.getString("CommonNames", null), new TypeToken<List<CommonName>>(){}.getType());
    }

    public void setScientificNames(List<ScientificName> names)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ScientificNames", gson.toJson(names));
        editor.apply();
    }

    public List<ScientificName> getScientificNames()
    {
        return gson.fromJson(prefs.getString("ScientificNames", null), new TypeToken<List<ScientificName>>(){}.getType());
    }

    public void setSiteTypes(List<SiteType> names)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("SiteTypes", gson.toJson(names));
        editor.apply();
    }

    public List<SiteType> getSiteTypes()
    {
        return gson.fromJson(prefs.getString("SiteTypes", null), new TypeToken<List<SiteType>>(){}.getType());
    }

    public void setAddMode(boolean state)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isAddMode", state);
        editor.apply();
    }

    public boolean isAddMode()
    {
        return prefs.getBoolean("isAddMode", false);
    }
}
