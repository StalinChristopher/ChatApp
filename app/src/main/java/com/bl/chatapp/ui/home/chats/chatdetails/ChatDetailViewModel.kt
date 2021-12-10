package com.bl.chatapp.ui.home.chats.chatdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.data.services.DatabaseLayer
import com.bl.chatapp.wrappers.MessageWrapper
import com.bl.chatapp.wrappers.UserDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@ExperimentalCoroutinesApi
class ChatDetailViewModel(private val currentUser: UserDetails, private val foreignUser: UserDetails) : ViewModel() {

    val messageList = ArrayList<Message>()
    private val databaseLayer = DatabaseLayer()

    private val _sendMessageStatus = MutableLiveData<Boolean>()
    val sendMessageStatus = _sendMessageStatus as LiveData<Boolean>

    private val _getMessageListStatus = MutableLiveData<Boolean>()
    val getMessageListStatus = _getMessageListStatus as LiveData<Boolean>

    init {
        getMessages(currentUser, foreignUser)
    }

    fun sendNewMessage(
        currentUser: UserDetails, foreignUser: UserDetails,
        messageText: String, messageType: String
    ) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val time = cal.timeInMillis
            var messageWrapper = MessageWrapper(messageText, time, messageType)
            val resultStatus = databaseLayer.sendNewMessage(currentUser, foreignUser, messageWrapper)
            if (resultStatus) {
                _sendMessageStatus.postValue(resultStatus)
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getMessages(currentUser: UserDetails, foreignUser: UserDetails) {
        viewModelScope.launch {
            databaseLayer.getMessageList(currentUser, foreignUser).collect {
                messageList.clear()
                messageList.addAll(it as ArrayList<Message>)
                _getMessageListStatus.postValue(true)
            }
        }
    }

    fun createChannel(currentUser: UserDetails, foreignUser: UserDetails) {
        viewModelScope.launch {
            val status = databaseLayer.createChatId(currentUser, foreignUser)
        }
    }
}