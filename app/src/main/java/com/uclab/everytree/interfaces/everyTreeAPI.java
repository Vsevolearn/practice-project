package com.uclab.everytree.interfaces;

import com.uclab.everytree.models.PageResponse;
import com.uclab.everytree.models.serializers.auth.Authorization;
import com.uclab.everytree.models.serializers.Photo;
import com.uclab.everytree.models.serializers.Record;
import com.uclab.everytree.models.serializers.Tree;
import com.uclab.everytree.models.serializers.auth.User;
import com.uclab.everytree.models.serializers.UserRecord;
import com.uclab.everytree.models.serializers.UserScore;
import com.uclab.everytree.models.serializers.UsersScores;
import com.uclab.everytree.models.serializers.spinner.CommonName;
import com.uclab.everytree.models.serializers.spinner.ScientificName;
import com.uclab.everytree.models.serializers.spinner.SiteType;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface everyTreeAPI {
    @POST("auth/sign-in/")
    Call<Authorization> signIn(@Body User body);

    @POST("auth/sign-up/")
    Call<Void> signUp(@Body User body);

    @POST("auth/refresh/")
    Call<Authorization> refresh(@Body Authorization body);

    @GET("trees/{lat}/{lng}/{z}/")
    Call<List<Tree>> getMarkers(@Path("lat") double lat, @Path("lng") double lng, @Path("z") int z);

    @POST("tree/")
    Call<Tree> postTree(@Body Tree tree);

    @GET("tree/{pk}/record/")
    Call<Record> getRecord(@Path("pk") int tree_id);

    @POST("record/")
    Call<Record> postRecord(@Body Record record);

    @GET("tree/{pk}/photos/")
    Call<List<Photo>>  getRecordPhotos(@Path("pk") int tree_id);

    @Multipart
    @POST("tree/{pk}/photos/")
    Call<Void>  postRecordPhotos(@Path("pk") int tree_id, @Part List<MultipartBody.Part> image);

    @GET("tree/{pk}/users/")
    Call<List<UserRecord>> getRecordUsers(@Path("pk") int tree_id);

    @GET("user/")
    Call<User> getUser();

    @GET("user/photos/")
    Call<List<Photo>>  getUserPhotos();

    @GET("user/trees/")
    Call<List<User>>  getUserTrees();

    @GET("user/score/")
    Call<List<UserScore>> getUserScore();

    @GET("users/scores/")
    Call<List<UsersScores>> getUsersScores();

    @GET("spinner/site-types/")
Call<List<SiteType>> getSiteTypes();

    @GET("spinner/common-names/")
    Call<List<CommonName>> getCommonNames();

    @GET("spinner/scientific-names/")
    Call<List<ScientificName>> getScientificNames();
}
