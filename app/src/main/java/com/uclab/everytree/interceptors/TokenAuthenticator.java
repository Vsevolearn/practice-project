package com.uclab.everytree.interceptors;

import android.content.Context;

import com.uclab.everytree.models.serializers.auth.Authorization;
import com.uclab.everytree.services.AuthService;
import com.uclab.everytree.services.NetworkService;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    private Context cxt;
    private AuthService service;

    public TokenAuthenticator(Context _cxt) {
        cxt = _cxt;
        service = new AuthService(cxt);
    }

    //gets called if server responds with 401 http response to retry authentication
    @Override
    public Request authenticate(Route route, Response response) throws IOException {

        if (service.getRefreshToken() == null || AuthService.isTokenExpired(service.getRefreshToken())) {
            return null; // Give up, we've already attempted to authenticate.
        }

        Authorization sendRefresh = new Authorization();
        sendRefresh.setRefreshToken(service.getRefreshToken());

            // Refresh your access_token using a synchronous api request
            retrofit2.Response<Authorization> res = NetworkService.getInstance(cxt)
                    .getEveryTreeAPI()
                    .refresh(sendRefresh).execute();

            if (res.body() != null && res.isSuccessful()) {
                Authorization auth = res.body();

                service.setAccessToken(auth.getAccessToken());

                // Add new header to rejected request and retry it
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + service.getAccessToken())
                        .build();
            }

            else {
                service.Clear();
                return null;
            }
    }
}
