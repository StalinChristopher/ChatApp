package com.bl.chatapp.ui.home.groups.groupdetails

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.chatapp.data.models.GroupInfo
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.data.services.DatabaseLayer
import com.bl.chatapp.wrappers.MessageWrapper
import com.bl.chatapp.wrappers.UserDetails
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class GroupChatDetailViewModel(
    private val currentUser: UserDetails,
    private val selectedGroup: GroupInfo
) : ViewModel() {

    val messageList = ArrayList<Message>()
    val memberList = ArrayList<UserDetails>()
    private val databaseLayer = DatabaseLayer()

    private val _sendMessageToGroupStatus = MutableLiveData<Boolean>()
    val sendMessageToGroupStatus = _sendMessageToGroupStatus as LiveData<Boolean>

    private val _getAllMessagesOfGroupStatus = MutableLiveData<Boolean>()
    val getAllMessageOfGroupStatus = _getAllMessagesOfGroupStatus as LiveData<Boolean>

    private val _getUserInfoFromParticipantsStatus = MutableLiveData<Boolean>()
    val getUserInfoFromParticipantsStatus = _getUserInfoFromParticipantsStatus as LiveData<Boolean>

    private val _groupChatImageUploadStatus = MutableLiveData<Boolean>()
    val groupChatImageUploadStatus = _groupChatImageUploadStatus as LiveData<Boolean>

    init {
        getALlMessagesOfGroup(selectedGroup)
        getUsersInfoFromParticipants(selectedGroup.participants)
    }

    fun sendMessageToGroup(
        currentUser: UserDetails,
        selectedGroup: GroupInfo,
        messageText: String,
        messageType: String
    ) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val time = cal.timeInMillis
            var messageWrapper = MessageWrapper(messageText, time, messageType)
            val status =
                databaseLayer.sendNewMessageToGroup(currentUser, selectedGroup, messageWrapper)
            if (status) {
                _sendMessageToGroupStatus.postValue(true)
            }
        }
    }

    private fun getALlMessagesOfGroup(group: GroupInfo) {
        viewModelScope.launch {
            databaseLayer.getMessageListOfGroup(group).collect {
                messageList.clear()
                messageList.addAll(it as ArrayList<Message>)
                _getAllMessagesOfGroupStatus.postValue(true)

            }
        }
    }

    private fun getUsersInfoFromParticipants(userIdList: ArrayList<String>) {
        viewModelScope.launch {
            val result = databaseLayer.getUsersInfoFromParticipants(userIdList)
            memberList.clear()
            memberList.addAll(result)
            _getUserInfoFromParticipantsStatus.postValue(true)
        }
    }

    fun uploadGroupChatImage(imageUri: Uri) {
        viewModelScope.launch {
            val result = databaseLayer.uploadGroupImageToCloud(imageUri, selectedGroup, currentUser)
            if(result) {
                Log.i("ChatDetailViewModel", "Image uploaded successfully and message sent")
                _groupChatImageUploadStatus.postValue(true)
            }
        }
    }
}