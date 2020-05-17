package com.example.watching_android.database

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.example.watching_android.MainActivity
import com.example.watching_android.model.*
import com.example.watching_android.ui.Chats
import com.example.watching_android.ui.RequestRecieved
import com.example.watching_android.ui.Search
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 *  This class has functions related to retrofit
 */
object RetrofitFunctions{

    /**
     * This function is used to register users on database
     */
    fun registerUser(userForRegistration: UserForRegistration, mainActivity: MainActivity){
        WatchingApi.service.postUsers(userForRegistration)
            .enqueue(object : Callback<UserWithApiKey> {
                override fun onFailure(call: Call<UserWithApiKey>, t: Throwable) {
                    mainActivity.onErrorRegister()
                }

                override fun onResponse(call: Call<UserWithApiKey>, response: Response<UserWithApiKey>) {
                    if (response.code() / 100 == 2) {
                        val userApiKey = response.body()?.apiKey
                        val id: Int = response.body()?.id ?: 0
                        // TODO: エラー処理

                        mainActivity.onResponseRegisterUser(UserWithApiKey(id, userApiKey))
                    } else {
                        mainActivity.onErrorRegister()
                    }
                }
            })
    }

    /**
     * This function is calling retrofit API and saving data as user shared preference
     */
    fun registerNickname(apiKey: String, userForUpdate: UserForUpdate, mainActivity: MainActivity){
        WatchingApi.service.putUsers(apiKey, userForUpdate)
            .enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    mainActivity.onErrorRegister()
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code() / 100 == 2) {
                        mainActivity.onResponseRegisterNickname(userForUpdate)
                    } else {
                        mainActivity.onErrorRegister()
                    }
                }
            })
    }

    /**
    * This function is calling retrofit API and saving data as user shared preference
    */
    fun sendMessageDescription(
        apiKey: String,
        eventForRegistration: EventForRegistration,
        chats: Chats,
        activity: Activity
    ){
        WatchingApi.service.postEvents(apiKey, eventForRegistration)
            .enqueue(object : Callback<Event>{
                override fun onFailure(call: Call<Event>, t: Throwable) {
                    chats.onError(activity)
                }

                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                    if (response.code() / 100 == 2) {
                        chats.onSuccess()

                    } else {
                        chats.onError(activity)
                    }
                }
            })
    }

    /**
     * This function is called when we search Person by entering phone number
     */
    fun getSearchResult(
        apiKey: String,
        phoneClass: String,
        activity: FragmentActivity,
        search: Search
    ){
        WatchingApi.service.getUsers(apiKey, phoneClass)
            .enqueue(object : Callback<UserPublic>{
                override fun onFailure(call: Call<UserPublic>, t: Throwable) {
                    search.onFailure(activity)
                }

                override fun onResponse(call: Call<UserPublic>, response: Response<UserPublic>) {
                    if (response.code() / 100 == 2) {
                        // レスポンスが null の時はきちんとエラー処理をすべき
                        response.body()?.let { search.sendRequest(it,activity) }
                    } else if (response.code() == 404) {
                        search.userNotFound(activity)
                    } else {
                        search.onFailure(activity)
                    }
                }
            })
    }

    /**
     * This function is called when send 見守りリクエスト
     */
    fun sendRequest(
        apiKey: String,
        userId: Int,
        activity: Activity,
        search: Search
    ){
        WatchingApi.service.postFollowRequests(apiKey, FollowRequestForRegistration(userId))
            .enqueue(object : Callback<Void>{
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    search.onFailure(activity)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code() / 100 == 2) {
                        search.onSuccess(activity)
                    } else {
                        search.onFailure(activity)
                    }
                }
            })
    }

    /**
     * This function is called to get the requests
     */
    fun getRequest(apiKey: String, activity: Activity, receivedRequestHolder: RequestRecieved?){
        WatchingApi.service.getFollowRequests(apiKey)
            .enqueue(object : Callback<List<FollowRequest>>{
                override fun onFailure(call: Call<List<FollowRequest>>, t: Throwable) {
                    //TODO: Decide what to do
                    receivedRequestHolder!!.onFailure( activity)
                }

                override fun onResponse(call: Call<List<FollowRequest>>, response: Response<List<FollowRequest>>) {
                    if (response.code() / 100 == 2) {
                        response.body()?.let { receivedRequestHolder!!.showRequests(it, activity) }
                    } else {
                        receivedRequestHolder!!.onFailure( activity)
                    }
                }
            })
    }

    /**
     * This function is called when a request is accepted
     */
    fun acceptRequest(
        apiKey: String,
        activity: Activity,
        id: Int,
        receivedRequestHolder: RequestRecieved?
    ){
        WatchingApi.service.postFollowRequestsAccept(apiKey, id)
            .enqueue(object : Callback<Void>{
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    //TODO: Decide what to do
                    receivedRequestHolder!!.onFailure(activity)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code() / 100 == 2) {
                        getRequest(apiKey, activity, receivedRequestHolder)
                        receivedRequestHolder!!.onSuccess(activity)
                    } else {
                        //TODO: Decide what to do
                        receivedRequestHolder!!.onFailure(activity)
                    }
                }
            })
    }

    /**
     * This function is called when a request is declined
     */
    fun declineRequest(
        apiKey: String,
        activity: Activity,
        id: Int,
        fragmentObject: RequestRecieved?
    ){
        WatchingApi.service.postFollowRequestsDecline(apiKey, id)
            .enqueue(object : Callback<Void>{
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    //TODO: Decide what to do
                    fragmentObject!!.onFailure(activity)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code() / 100 == 2) {
                        getRequest(apiKey, activity, fragmentObject)
                        fragmentObject!!.onRequestDeclinedSuccess(activity)
                    } else {
                        //TODO: Decide what to do
                        fragmentObject!!.onFailure(activity)
                    }
                }
            })
    }

}
