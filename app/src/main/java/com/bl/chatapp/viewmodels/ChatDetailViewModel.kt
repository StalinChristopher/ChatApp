package com.bl.chatapp.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.data.services.DatabaseLayer
import com.bl.chatapp.wrappers.MessageWrapper
import com.bl.chatapp.wrappers.UserDetails
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ChatDetailViewModel : ViewModel() {

    private val _sendMessageStatus = MutableLiveData<Boolean>()
    val sendMessageStatus = _sendMessageStatus as LiveData<Boolean>

    private val _getMessageListStatus = MutableLiveData<ArrayList<Message>>()
    val getMessageListStatus = _getMessageListStatus as LiveData<ArrayList<Message>>

    fun sendNewMessage(
        context: Context, currentUser: UserDetails, foreignUser: UserDetails,
        messageText: String, messageType: String
    ) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val time = cal.timeInMillis
            var messageWrapper = MessageWrapper(messageText, time, messageType)
            val resultStatus = DatabaseLayer.getInstance(context)
                .sendNewMessage(currentUser, foreignUser, messageWrapper)
            if (resultStatus) {
                _sendMessageStatus.postValue(resultStatus)
            }
        }
    }

//    fun getMessages(context: Context, currentUser: UserDetails, foreignUser: UserDetails) {
//        viewModelScope.launch {
//            val messageList = DatabaseLayer.getInstance(context).getMessageList(currentUser, foreignUser)
//            if(messageList != null) {
//                _getMessageListStatus.postValue(messageList)
//            }
//        }
//    }

    fun createChannel(context: Context, currentUser: UserDetails, foreignUser: UserDetails) {
        viewModelScope.launch {
            val status = DatabaseLayer.getInstance(context).createChatId(currentUser, foreignUser)
        }
    }
}