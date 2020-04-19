package com.example.watching_android.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.watching_android.database.JsonPlaceHolder
import com.example.watching_android.database.RetrofitConnection
import com.example.watching_android.model.Messages
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

// This class will call and store messages which we get from API
@Singleton
class MessageRepository @Inject constructor(
    private val webservice : JsonPlaceHolder
){

    fun getMessages() : LiveData<Messages>{
        val data = MutableLiveData<Messages>()
        if (webservice != null) {
            val retrofitConnection = RetrofitConnection()
            retrofitConnection.getMessgaes().enqueue(object : Callback<Messages>{
                override fun onFailure(call: Call<Messages>, t: Throwable) {
                    TODO("Not yet implemented")
                }

                override fun onResponse(call: Call<Messages>, response: Response<Messages>) {
                   data.value = response.body()
                }
            })
        }
        return data
    }
}