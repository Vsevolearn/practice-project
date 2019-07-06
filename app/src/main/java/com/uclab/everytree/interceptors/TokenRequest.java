package com.uclab.everytree.interceptors;

import android.content.Context;

import com.uclab.everytree.services.AuthService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenRequest implements Interceptor {
    private AuthService service;

    public TokenRequest(Context _cxt)
    {
        service = new AuthService(_cxt);
    }

    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        //if we have access token
        if (service.getAccessToken() != null && !AuthService.isTokenExpired(service.getAccessToken())) {
            Request.Builder builder = request.newBuilder()
                    .header("Authorization", "Bearer " + service.getAccessToken());
            request = builder.build();
        }

        return chain.proceed(request);
    }
}
