package com.example.watching_android.database

import com.example.watching_android.model.*
import retrofit2.Call
import retrofit2.http.*

/**
 * Watching API.
 *
 * - https://app.swaggerhub.com/apis-docs/kyuuki/Watching/1.0.0
 */
interface WatchingApiService {
    companion object {
        const val HEADER_NAME_X_API_KEY = "x-api-key"
    }

    /*
     * user
     */
    @GET("users")
    fun getUsers(@Header(HEADER_NAME_X_API_KEY) xApiKey: String, @Query("phone_number") phoneNumber: String): Call<UserPublic>

    @POST("users")
    fun postUsers(@Body userForRegistration: UserForRegistration): Call<UserWithApiKey>

    @PUT("users")
    fun putUsers(@Header(HEADER_NAME_X_API_KEY) xApiKey: String, @Body nickName: UserForUpdate): Call<Void>

    /*
     * event
     */
    @GET("events")
    fun getEvents(@Header(HEADER_NAME_X_API_KEY) xApiKey: String): Call<List<Event>>

    @POST("events")
    fun postEvents(@Header(HEADER_NAME_X_API_KEY) xApiKey: String, @Body eventForRegistration: EventForRegistration): Call<Event>

    /*
     * follow_request
     */
    @GET("follow_requests")
    fun getFollowRequests(@Header(HEADER_NAME_X_API_KEY) xApiKey: String): Call<List<FollowRequest>>

    @POST("follow_requests")
    fun postFollowRequests(@Header(HEADER_NAME_X_API_KEY) xApiKey: String, @Body user_forRegistrationFollow: FollowRequestForRegistration): Call<Void>

    @POST("follow_requests/{id}/accept")
    fun postFollowRequestsAccept(@Header(HEADER_NAME_X_API_KEY) xApiKey: String, @Path("id") id: Int): Call<Void>

    @POST("follow_requests/{id}/decline")
    fun postFollowRequestsDecline(@Header(HEADER_NAME_X_API_KEY) xApiKey: String, @Path("id") id: Int): Call<Void>

}