package com.example.watching_android.database

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.example.watching_android.MainActivity
import com.example.watching_android.model.*
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
    fun registerUser(userInfoData: UserInfoData, activity: Activity){

        val mainActivity = MainActivity()
        val resUserInfoData = UserRegistration(0, "")
        WatchingApi.service.postUsers(userInfoData)
            .enqueue(object : Callback<UserRegistration>{
                override fun onFailure(call: Call<UserRegistration>, t: Throwable) {
                    mainActivity.getResponse(null, null, activity)
                }

                override fun onResponse(call: Call<UserRegistration>, response: Response<UserRegistration>) {
                    val responseCode = Integer.parseInt(response.code().toString().substring(0, 1))
                    if (responseCode ==2 ){
                        val userApiKey = response.body()?.api_key ?:""
                        val id : Int = response.body()?.id ?:0
                        resUserInfoData.id = id
                        resUserInfoData.api_key = userApiKey
                        mainActivity.getResponse(resUserInfoData, null, activity)
                    }
                    else{
                        mainActivity.getResponse(null, null, activity)
                    }
                }
            })

    }

    /**
     * This function is calling retrofit API and saving data as user shared preference
     */
    fun registerNickName(nickName: NickNameData, activity: Activity){
        val mainActivity = MainActivity()
        WatchingApi.service.putUsers(Preferences.apiKey, nickName)
            .enqueue(object : Callback<Void>{
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    mainActivity.getResponse(null, null, activity)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    val responseCode = Integer.parseInt(response.code().toString().substring(0, 1))
                    if (responseCode ==2 ){
                        mainActivity.getResponse(null, nickName, activity)
                    }
                    else{
                        mainActivity.getResponse(null, null, activity)
                    }

                }

            })
    }

    /**
    * This function is calling retrofit API and saving data as user shared preference
    */
    fun sendMessageDescription(messageDescription: MessageDescription){
        WatchingApi.service.postEvents(Preferences.apiKey, messageDescription)
            .enqueue(object : Callback<Messages>{
                override fun onFailure(call: Call<Messages>, t: Throwable) {
                    //mainActivity.getResponse(null, null, activity)
                    //TODO: Decide what to do
                }

                override fun onResponse(call: Call<Messages>, response: Response<Messages>) {
                    val responseCode = Integer.parseInt(response.code().toString().substring(0, 1))
                    if (responseCode ==2 ){
                        //Do Nothing
                    }
                    else{
                        //TODO: Decide what to do
                    }
                }

            })
    }

    /**
     * This function is called when we search Person by entering phone number
     */
    fun getSearchResult(phoneClass: String, activity: FragmentActivity){
        val search = Search()
        WatchingApi.service.getUsers(Preferences.apiKey, phoneClass)
            .enqueue(object : Callback<NickNameID>{
                override fun onFailure(call: Call<NickNameID>, t: Throwable) {
                    search.onFailure(activity)
                }

                override fun onResponse(call: Call<NickNameID>, response: Response<NickNameID>) {
                    val responseCode = Integer.parseInt(response.code().toString().substring(0, 1))
                    if (responseCode ==2 ){
                        response.body()?.let { search.sendRequest(it,activity) }
                    }
                    else{
                        search.onFailure(activity)
                    }
                }

            })
    }

    /**
     * This function is called when send 見守りリクエスト
     */
    fun sendRequest(userId : Int, activity: Activity){
        val search = Search()
        WatchingApi.service.postFollowRequests(Preferences.apiKey, RequestId(userId))
            .enqueue(object : Callback<Void>{
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    search.onFailure(activity)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    val responseCode = Integer.parseInt(response.code().toString().substring(0, 1))
                    if (responseCode ==2 ){
                        search.onSuccess(activity)
                    }
                    else{
                        search.onFailure(activity)
                    }

                }

            })
    }

    /**
     * This function is called to get the requests
     */
    fun getRequest(activity: Activity){
        val requestRecieved = RequestRecieved()
        WatchingApi.service.getFollowRequests(Preferences.apiKey)
            .enqueue(object : Callback<List<RequestRecievedModel>>{
                override fun onFailure(call: Call<List<RequestRecievedModel>>, t: Throwable) {
                    //TODO: Decide what to do
                    requestRecieved.onFailure( activity)
                }

                override fun onResponse(call: Call<List<RequestRecievedModel>>, response: Response<List<RequestRecievedModel>>) {
                    val responseCode = Integer.parseInt(response.code().toString().substring(0, 1))
                    if (responseCode ==2 ){
                        response.body()?.let { requestRecieved.showRequests(it, activity) }
                    }
                    else{
                        requestRecieved.onFailure( activity)
                    }

                }

            })
    }

    /**
     * This function is called when a request is accepted
     */
    fun acceptRequest(activity: Activity, id : Int){
        val requestRecieved = RequestRecieved()
        WatchingApi.service.postFollowRequestsAccept(Preferences.apiKey, id)
            .enqueue(object : Callback<Void>{
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    //TODO: Decide what to do
                    requestRecieved.onFailure(activity)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    val responseCode = Integer.parseInt(response.code().toString().substring(0, 1))
                    if (responseCode ==2 ){
                        getRequest(activity)
                        requestRecieved.onSuccess(activity)
                    }
                    else{
                        //TODO: Decide what to do
                        requestRecieved.onFailure(activity)
                    }

                }

            })
    }

    /**
     * This function is called when a request is declined
     */
    fun declineRequest(activity: Activity, id : Int){
        val requestRecieved = RequestRecieved()
        WatchingApi.service.postFollowRequestsDecline(Preferences.apiKey, id)
            .enqueue(object : Callback<Void>{
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    //TODO: Decide what to do
                    requestRecieved.onFailure(activity)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    val responseCode = Integer.parseInt(response.code().toString().substring(0, 1))
                    if (responseCode ==2 ){
                        getRequest(activity)
                        requestRecieved.onRequestDeclinedSuccess(activity)
                    }
                    else{
                        //TODO: Decide what to do
                        requestRecieved.onFailure(activity)
                    }

                }

            })
    }

}
