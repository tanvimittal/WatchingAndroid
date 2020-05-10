package com.example.watching_android.database

import com.example.watching_android.model.*
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface JsonPlaceHolder {

    @GET("event")
    fun getPosts(): Call<List<POST>>

    @POST("users")
    fun createUser(@Body userInfoData: UserInfoData): Call<UserRegistration>

    @PUT( "users")
    fun updateNickName(@Header("x-api-key") xApiKey: String,@Body nickName: NickNameData) : Call<Void>

    @GET("events")
    fun getMessgaes(@Header("x-api-key") xApiKey: String): Call<List<Messages>>

    @POST("events")
    fun sendMessageDescription(@Header("x-api-key") xApiKey: String,@Body messageDescription: MessageDescription): Call<Messages>

    @GET("users")
    fun getSearchResult(@Header("x-api-key") xApiKey: String, @Query("phone_number") phoneNumber: String): Call<NickNameID>

    @POST("follow_requests")
    fun sendRequest(@Header("x-api-key") xApiKey: String,@Body user_id: RequestId): Call<Void>

    @GET("follow_requests")
    fun getRequests(@Header("x-api-key") xApiKey: String): Call<List<RequestRecievedModel>>

    @POST("follow_requests/{id}/accept")
    fun acceptRequest(@Header("x-api-key") xApiKey: String, @Path("id")id: Int): Call<Void>

    @POST("follow_requests/{id}/decline")
    fun declineRequest(@Header("x-api-key") xApiKey: String, @Path("id")id: Int): Call<Void>

}