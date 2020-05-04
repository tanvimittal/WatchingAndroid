package com.example.watching_android.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.watching_android.database.JsonPlaceHolder
import com.example.watching_android.database.Preferences
import com.example.watching_android.database.RetrofitConnection
import com.example.watching_android.database.RetrofitFunctions
import com.example.watching_android.model.Messages
import com.example.watching_android.ui.MessageViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

// This class will call and store messages which we get from API
@Singleton
class MessageRepository(
    private val webservice : JsonPlaceHolder
){
// At first, We don't need to use Dagger
//class MessageRepository @Inject constructor(
//    private val webservice : JsonPlaceHolder
//){

    //fun getMessages() : LiveData<List<Messages>>{
    fun getMessages(messageViewModel: MessageViewModel) : List<Messages>{
        var data = mutableListOf<Messages>()
            val retrofitConnection = RetrofitConnection()
            retrofitConnection.getMessgaes(Preferences.APIKEY).enqueue(object : Callback<List<Messages>>{
                override fun onFailure(call: Call<List<Messages>>, t: Throwable) {
                    //TODO: Decide on Failure
                }

                override fun onResponse(call: Call<List<Messages>>, response: Response<List<Messages>>) {
                   data = response.body() as MutableList<Messages>
                    messageViewModel.setMessageValue(data)
                }
            })
        return data
    }

}
