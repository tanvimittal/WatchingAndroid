package com.example.watching_android.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.watching_android.model.Messages
import com.example.watching_android.repository.MessageRepository
import javax.inject.Inject

class MessageViewModel @Inject constructor (
    savedStateHandle: SavedStateHandle,
    messageRepository: MessageRepository) : ViewModel() {

    //val api_key : String = savedStateHandle["api_key"] ?: throw IllegalArgumentException("missing api key")
    //val user_id : Int = savedStateHandle["api_key"] ?: throw IllegalArgumentException("missing user id")
    val messages : LiveData<Messages> = messageRepository.getMessages()
}