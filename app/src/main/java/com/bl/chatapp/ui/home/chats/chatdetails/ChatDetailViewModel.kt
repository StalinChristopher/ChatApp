package com.bl.chatapp.ui.home.chats.chatdetails

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.chatapp.common.Constants.IMAGE
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.data.datalayer.DatabaseLayer
import com.bl.chatapp.wrappers.MessageWrapper
import com.bl.chatapp.wrappers.UserDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@ExperimentalCoroutinesApi
class ChatDetailViewModel(private val currentUser: UserDetails, private val foreignUser: UserDetails) : ViewModel() {

    var messageList = ArrayList<Message>()
    private val databaseLayer = DatabaseLayer()

    private val _sendMessageStatus = MutableLiveData<Message>()
    val sendMessageStatus = _sendMessageStatus as LiveData<Message>

    private val _getMessageListStatus = MutableLiveData<ArrayList<Message>>()
    val getMessageListStatus = _getMessageListStatus as LiveData<ArrayList<Message>>

    private val _chatImageUploadStatus = MutableLiveData<Message>()
    val chatImageUploadStatus = _chatImageUploadStatus as LiveData<Message>

    private val _getPagedMessagesStatus = MutableLiveData<ArrayList<Message>>()
    val getPagedMessageStatus = _getPagedMessagesStatus as LiveData<ArrayList<Message>>

    init {
        getMessages(currentUser, foreignUser)
    }

    fun sendNewMessage(
        currentUser: UserDetails, foreignUser: UserDetails,
        messageText: String, messageType: String
    ) {
        viewModelScope.launch {
            val time = System.currentTimeMillis()
            var messageWrapper = MessageWrapper(messageText, time, messageType)
            val resultMessage = databaseLayer.sendNewMessage(currentUser, foreignUser, messageWrapper)
            if (resultMessage != null) {
                _sendMessageStatus.postValue(resultMessage)
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getMessages(currentUser: UserDetails, foreignUser: UserDetails) {
        viewModelScope.launch {
            databaseLayer.getMessageList(currentUser, foreignUser).collect {
                Log.i("ChatDetailViewModel", "messages -> $it")
                messageList.clear()
                if (it != null) {
                    messageList.addAll(it)
                }
                _getMessageListStatus.postValue(it)

            }
        }
    }

    fun createChannel(currentUser: UserDetails, foreignUser: UserDetails) {
        viewModelScope.launch {
            val status = databaseLayer.createChatId(currentUser, foreignUser)
        }
    }

    fun uploadChatImageToCloud(imageUri: Uri) {
        viewModelScope.launch {
            val result = databaseLayer.uploadChatImageToCloud(imageUri, currentUser, foreignUser)
            if(result != null) {
                Log.i("ChatDetailViewModel", "Image uploaded successfully and message sent")
                _chatImageUploadStatus.postValue(result)
            }
        }
    }

    fun sendPushNotification(message: Message) {
        viewModelScope.launch {
            if(message.messageType == IMAGE) {
                databaseLayer.sendNotificationToUser(foreignUser.firebaseTokenId, currentUser.userName, "", message.content)
            } else {
                databaseLayer.sendNotificationToUser(foreignUser.firebaseTokenId, currentUser.userName, message.content, "")
            }
        }
    }

    fun getPagedMessages(currentUser: UserDetails, foreignUser: UserDetails, offset: Long) {
        viewModelScope.launch {
            val pagedMessageList = databaseLayer.getPagedMessages(currentUser, foreignUser, offset)
            if(pagedMessageList != null) {
                _getPagedMessagesStatus.postValue(pagedMessageList)
            }
        }
    }
}