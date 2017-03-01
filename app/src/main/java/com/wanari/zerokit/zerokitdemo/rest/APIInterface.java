package com.wanari.zerokit.zerokitdemo.rest;

import com.wanari.zerokit.zerokitdemo.rest.entities.ApproveInvitationAcceptionJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.ApproveInvitationCreationJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.ApproveShareJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.ApproveTresorCreationJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.InitUserResponseJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.UserJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.UserResponseJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.ValidateUserRequestJson;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

public interface APIInterface {

    @POST("/inituser")
    Observable<InitUserResponseJson> initUserRegistration();

    @POST("/validate")
    Observable<ResponseBody> validateUserRegistration(@Body ValidateUserRequestJson requestJson);

    @POST("/approve/tresor")
    Observable<ResponseBody> approveTresorCreation(@Body ApproveTresorCreationJson requestJson);

    @POST("/approve/share")
    Observable<ResponseBody> approveShare(@Body ApproveShareJson requestJson);

    @POST("/approve/invitation/creation")
    Observable<ResponseBody> approveInvitationCreation(@Body ApproveInvitationCreationJson requestJson);

    @POST("/approve/invitation/acception")
    Observable<ResponseBody> approveInvitationAcception(@Body ApproveInvitationAcceptionJson requestJson);

    @GET("/usernames")
    Observable<List<UserJson>> getUsers();

    @GET("/user/{username}")
    Observable<UserJson> getUserIdByUserName(@Path("username") String username);
}
