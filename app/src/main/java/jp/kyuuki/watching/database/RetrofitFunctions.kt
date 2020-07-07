package jp.kyuuki.watching.database

import android.app.Activity
import android.app.Presentation
import androidx.fragment.app.FragmentActivity
import jp.kyuuki.watching.MainActivity
import jp.kyuuki.watching.model.*
import jp.kyuuki.watching.ui.EventsFragment
import jp.kyuuki.watching.ui.RecievedRequestsFragment
import jp.kyuuki.watching.ui.UserSearchFragment
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
                        val userWithApiKey = response.body()
                        // Checking on error
                        if (userWithApiKey == null) {
                            mainActivity.onErrorRegister()
                            // IF API key is null
                        } else if (userWithApiKey.apiKey == null) {
                            mainActivity.onErrorRegister()
                            // If id is less than equal to 0
                        } else if (userWithApiKey.id <= 0) {
                            mainActivity.onErrorRegister()
                            // When there is no error
                        } else {
                            mainActivity.onResponseRegisterUser(userWithApiKey)
                        }

                    } else {
                        mainActivity.onErrorRegister()
                    }
                }
            })
    }

    /**
     * This function is calling retrofit API and saving data as user shared preference
     */
    fun registerNickname(apiKey: String, userForUpdate: UserForUpdate, mainActivity: MainActivity) {
        WatchingApi.service.putUsers(apiKey, userForUpdate).enqueue(object : Callback<Void> {
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
     * @param - apiKey : corresponding key for user
     *          fcmToken : Token to be registered
     * @return : 200 on success
     */
    fun registerFcmToken(apiKey: String, fcmToken: UserForFcmToken) {

        WatchingApi.service.putUsersFcmToken(apiKey, fcmToken).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {

            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.code() / 100 == 2) {

                } else {

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
        eventsFragment: EventsFragment,
        activity: Activity
    ){
        WatchingApi.service.postEvents(apiKey, eventForRegistration)
            .enqueue(object : Callback<Event>{
                override fun onFailure(call: Call<Event>, t: Throwable) {
                    eventsFragment.onError(activity)
                }

                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                    if (response.code() / 100 == 2) {
                        eventsFragment.onSuccess()
                    } else {
                        eventsFragment.onError(activity)
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
        userSearchFragment: UserSearchFragment
    ){
        WatchingApi.service.getUsers(apiKey, phoneClass)
            .enqueue(object : Callback<UserPublic>{
                override fun onFailure(call: Call<UserPublic>, t: Throwable) {
                    userSearchFragment.onFailure(activity)
                }

                override fun onResponse(call: Call<UserPublic>, response: Response<UserPublic>) {
                    if (response.code() / 100 == 2) {
                        // レスポンスが null の時はきちんとエラー処理をすべき
                        response.body()?.let { userSearchFragment.sendRequest(it,activity) }
                    } else if (response.code() == 404) {
                        userSearchFragment.userNotFound(activity)
                    } else {
                        userSearchFragment.onFailure(activity)
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
        userSearchFragment: UserSearchFragment
    ){
        WatchingApi.service.postFollowRequests(apiKey, FollowRequestForRegistration(userId))
            .enqueue(object : Callback<Void>{
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    userSearchFragment.onFailure(activity)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code() / 100 == 2) {
                        userSearchFragment.onSuccess(activity)
                    } else {
                        userSearchFragment.onFailure(activity)
                    }
                }
            })
    }

    /**
     * This function is called to get the requests
     */
    fun getRequest(apiKey: String, activity: Activity, receivedRequestHolder: RecievedRequestsFragment?){
        WatchingApi.service.getFollowRequests(apiKey)
            .enqueue(object : Callback<List<FollowRequest>>{
                override fun onFailure(call: Call<List<FollowRequest>>, t: Throwable) {
                    //TODO: Decide what to do
                    receivedRequestHolder!!.onFailure( activity)
                }

                override fun onResponse(call: Call<List<FollowRequest>>, response: Response<List<FollowRequest>>) {
                    if (response.code() / 100 == 2) {
                        //TODO:
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
        receivedRequestHolder: RecievedRequestsFragment?
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
        fragmentObject: RecievedRequestsFragment?
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
