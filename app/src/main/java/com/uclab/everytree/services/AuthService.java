package com.uclab.everytree.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.uclab.everytree.models.serializers.auth.User;

public class AuthService {
    private SharedPreferences prefs;
    private Gson gson = new Gson();
    private Context cxt;

    public AuthService(Context _cxt) {
        cxt = _cxt;
        prefs = PreferenceManager.getDefaultSharedPreferences(cxt);
    }

    public void setUser(User user)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("User", gson.toJson(user));
        editor.apply();
    }

    public User getUser()
    {
        return gson.fromJson(prefs.getString("User", null), User.class);
    }

    public void setAccessToken(String token)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("accessToken", token);
        editor.apply();
    }

    public String getAccessToken()
    {
        return prefs.getString("accessToken", null);
    }

    public void setRefreshToken(String token)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("refreshToken", token);
        editor.apply();
    }

    public String getRefreshToken()
    {
        return prefs.getString("refreshToken", null);
    }

    public void setIsAuthorized(Boolean state)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isAuthorized", state);
        editor.apply();
    }

    public static boolean isTokenExpired(String token)
    {
        if (token != null) {
            JWT jwt = new JWT(token);
            return jwt.isExpired(5);
        }

        return true;
    }

    public boolean isAuthorized()
    {
        boolean state = prefs.getBoolean("isAuthorized", false);

        if (!state)
        {
            Toast.makeText(cxt, "At first you need to authorize!", Toast.LENGTH_LONG).show();
        }

        return state;
    }

    public void Clear()
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("accessToken");
        editor.remove("refreshToken");
        editor.remove("User");
        editor.remove("isAuthorized");
        editor.apply();
    }
}
