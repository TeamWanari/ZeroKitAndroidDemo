package com.wanari.zerokit.zerokitdemo.rest;

import com.wanari.zerokit.zerokitdemo.rest.entities.ApproveTresorCreationJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.InitUserResponseJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.ValidateUserRequestJson;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface APIInterface {

    @POST("/inituser")
    Observable<InitUserResponseJson> initUserRegistration();

    @POST("/validate")
    Observable<ResponseBody> validateUserRegistration(@Body ValidateUserRequestJson requestJson);

    @POST("/approvetresorcreation")
    Observable<ApproveTresorCreationJson> approveTresorCreation(@Body ApproveTresorCreationJson requestJson);
}
