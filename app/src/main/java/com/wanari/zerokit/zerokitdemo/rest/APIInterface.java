package com.wanari.zerokit.zerokitdemo.rest;

import com.wanari.zerokit.zerokitdemo.rest.entities.ApproveShareJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.ApproveTresorCreationJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.InitUserResponseJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.UserResponseJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.ValidateUserRequestJson;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface APIInterface {

    @POST("/inituser")
    Observable<InitUserResponseJson> initUserRegistration();

    @POST("/validate")
    Observable<ResponseBody> validateUserRegistration(@Body ValidateUserRequestJson requestJson);

    @POST("/approvetresor")
    Observable<ResponseBody> approveTresorCreation(@Body ApproveTresorCreationJson requestJson);

    @POST("/approveshare")
    Observable<ResponseBody> approveShare(@Body ApproveShareJson requestJson);

    @GET("/users")
    Observable<UserResponseJson> getUsers();
}
