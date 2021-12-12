package com.bl.chatapp.ui.home.groups.groupdetails

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.chatapp.common.Constants.IMAGE
import com.bl.chatapp.data.models.GroupInfo
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.data.datalayer.DatabaseLayer
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

    private val _sendMessageToGroupStatus = MutableLiveData<Message>()
    val sendMessageToGroupStatus = _sendMessageToGroupStatus as LiveData<Message>

    private val _getAllMessagesOfGroupStatus = MutableLiveData<Boolean>()
    val getAllMessageOfGroupStatus = _getAllMessagesOfGroupStatus as LiveData<Boolean>

    private val _getUserInfoFromParticipantsStatus = MutableLiveData<Boolean>()
    val getUserInfoFromParticipantsStatus = _getUserInfoFromParticipantsStatus as LiveData<Boolean>

    private val _groupChatImageUploadStatus = MutableLiveData<Message>()
    val groupChatImageUploadStatus = _groupChatImageUploadStatus as LiveData<Message>

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
            val resultMessage =
                databaseLayer.sendNewMessageToGroup(currentUser, selectedGroup, messageWrapper)
            if (resultMessage != null) {
                _sendMessageToGroupStatus.postValue(resultMessage)
            }
        }
    }

    private fun getALlMessagesOfGroup(group: GroupInfo) {
        viewModelScope.launch {
            databaseLayer.getMessageListOfGroup(group).collect {
                messageList.clear()
                messageList.addAll(it as ArrayList<Message>)
                messageList.reverse()
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
            val resultMessage = databaseLayer.uploadGroupImageToCloud(imageUri, selectedGroup, currentUser)
            if(resultMessage != null ) {
                Log.i("ChatDetailViewModel", "Image uploaded successfully and message sent")
                _groupChatImageUploadStatus.postValue(resultMessage)
            }
        }
    }

    fun sendGroupNotifications(message: Message) {
        viewModelScope.launch {
            var membersTokenList = ArrayList<String>()
            Log.i("GroupChatDetailViewModel","$memberList")
            memberList.forEach {
                membersTokenList.add(it.firebaseTokenId)
            }
            Log.i("GroupChatDetailViewModel","$membersTokenList")
            if(message.messageType == IMAGE) {
                databaseLayer.sendNotificationToGroup(membersTokenList, currentUser.userName, "", message.content)
            } else {
                databaseLayer.sendNotificationToGroup(membersTokenList, currentUser.userName, message.content, "")
            }
        }
    }
}