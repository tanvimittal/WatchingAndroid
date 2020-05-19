package jp.kyuuki.watching.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import jp.kyuuki.watching.database.WatchingApi
import jp.kyuuki.watching.model.Event
import jp.kyuuki.watching.repository.MessageRepository

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
    val messages = MutableLiveData<List<Event>>()

    fun getRecentMessages(apiKey: String) {
        messageRepository.getMessages(apiKey, this)
    }

    fun setMessageValue(paramMessages : List<Event>){
        messages.value = paramMessages
    }

}