package com.example.watching_android.repository

import com.example.watching_android.database.*
import com.example.watching_android.model.Event
import com.example.watching_android.ui.MessageViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Singleton

// This class will call and store messages which we get from API
@Singleton
class MessageRepository(
    private val webservice : WatchingApiService
){
// At first, We don't need to use Dagger
//class MessageRepository @Inject constructor(
//    private val webservice : JsonPlaceHolder
//){

    //fun getMessages() : LiveData<List<Messages>>{
    fun getMessages(apiKey: String, messageViewModel: MessageViewModel) : List<Event>{
        var data = mutableListOf<Event>()

        webservice.getEvents(apiKey).enqueue(object : Callback<List<Event>>{
                override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                    //TODO: Decide on Failure
                }

                override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                    if (response.code() / 100 == 2) {
                        data = response.body() as MutableList<Event>
                        messageViewModel.setMessageValue(data)
                    }
                }
            })
        return data
    }

}
