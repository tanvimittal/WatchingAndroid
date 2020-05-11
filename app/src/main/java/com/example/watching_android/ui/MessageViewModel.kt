package com.example.watching_android.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.watching_android.database.WatchingApi
import com.example.watching_android.model.Messages
import com.example.watching_android.repository.MessageRepository

// https://qiita.com/Tsuyoshi_Murakami/items/12678bc77d9a9f2e5813#dagger%E3%82%92%E6%8D%A8%E3%81%A6%E3%82%8B
// https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate

class MessageViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
// At first, We don't need to use Dagger, But I want to pass Repository to this
//class MessageViewModel @Inject constructor (
//    savedStateHandle: SavedStateHandle,
//    messageRepository: MessageRepository) : ViewModel() {
    private val messageRepository = MessageRepository(WatchingApi.service)

    //private val messages = MutableLiveData<List<Messages>>()
    //val api_key : String = savedStateHandle["api_key"] ?: throw IllegalArgumentException("missing api key")
    //val user_id : Int = savedStateHandle["api_key"] ?: throw IllegalArgumentException("missing user id")
    //private lateinit var messages : LiveData<List<Messages>>// = messageRepository.getMessages()
    val messages = MutableLiveData<List<Messages>>()

    fun getRecentMessages(){
        messageRepository.getMessages(this)
    }

    fun setMessageValue(paramMessages : List<Messages>){
        messages.value = paramMessages
    }

}