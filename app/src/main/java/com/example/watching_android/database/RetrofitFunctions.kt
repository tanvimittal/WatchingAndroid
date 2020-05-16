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

     lateinit var userApiKey: String
    /**
     * This function is used to register users on database
     */
    fun registerUser(userInfoData: UserInfoData, activity: Activity, mainActivity: MainActivity){

        val resUserInfoData = UserRegistration(0, "")
        WatchingApi.service.postUsers(userInfoData)
            .enqueue(object : Callback<UserRegistration>{
                override fun onFailure(call: Call<UserRegistration>, t: Throwable) {
                    mainActivity.getResponse(null, null, activity)
                }

                override fun onResponse(call: Call<UserRegistration>, response: Response<UserRegistration>) {
                    if (response.code() / 100 == 2) {
                        val userApiKey = response.body()?.api_key ?:""
                        val id : Int = response.body()?.id ?:0
                        resUserInfoData.id = id
                        resUserInfoData.api_key = userApiKey
                        mainActivity.getResponse(resUserInfoData, null, activity)
                    } else {
                        mainActivity.getResponse(null, null, activity)
                    }
                }
            })
    }

    /**
     * This function is calling retrofit API and saving data as user shared preference
     */
    fun registerNickName(apiKey: String, nickName: NickNameData, activity: Activity, mainActivity: MainActivity){
        WatchingApi.service.putUsers(apiKey, nickName)
            .enqueue(object : Callback<Void>{
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    mainActivity.getResponse(null, null, activity)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code() / 100 == 2) {
                        mainActivity.getResponse(null, nickName, activity)
                    } else {
                        mainActivity.getResponse(null, null, activity)
                    }
                }
            })
    }

    /**
    * This function is calling retrofit API and saving data as user shared preference
    */
    fun sendMessageDescription(
        apiKey: String,
        messageDescription: MessageDescription,
        chats: Chats,
        activity: Activity
    ){
        WatchingApi.service.postEvents(apiKey, messageDescription)
            .enqueue(object : Callback<Messages>{
                override fun onFailure(call: Call<Messages>, t: Throwable) {
                    chats.onError(activity)
                }

                override fun onResponse(call: Call<Messages>, response: Response<Messages>) {
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
            .enqueue(object : Callback<NickNameID>{
                override fun onFailure(call: Call<NickNameID>, t: Throwable) {
                    search.onFailure(activity)
                }

                override fun onResponse(call: Call<NickNameID>, response: Response<NickNameID>) {
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
        WatchingApi.service.postFollowRequests(apiKey, RequestId(userId))
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
            .enqueue(object : Callback<List<RequestRecievedModel>>{
                override fun onFailure(call: Call<List<RequestRecievedModel>>, t: Throwable) {
                    //TODO: Decide what to do
                    receivedRequestHolder!!.onFailure( activity)
                }

                override fun onResponse(call: Call<List<RequestRecievedModel>>, response: Response<List<RequestRecievedModel>>) {
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
