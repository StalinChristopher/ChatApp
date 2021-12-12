package com.bl.chatapp.data.services

import android.net.Uri
import android.util.Log
import com.bl.chatapp.common.Constants.FIREBASE_CHAT_IMAGES
import com.bl.chatapp.common.Constants.FIREBASE_GROUP_CHAT_IMAGES
import com.bl.chatapp.common.Constants.IMAGE
import com.bl.chatapp.common.Utilities
import com.bl.chatapp.data.models.Chat
import com.bl.chatapp.data.models.GroupInfo
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.wrappers.MessageWrapper
import com.bl.chatapp.wrappers.UserDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class DatabaseLayer() {
    private var fireStoreDb: FirebaseDatabaseService = FirebaseDatabaseService()
    private var firebaseStorage: FirebaseStorage = FirebaseStorage()

//    companion object {
//        private val instance: DatabaseLayer? by lazy { null }
//        fun getInstance(context: Context): DatabaseLayer = instance ?: DatabaseLayer(context)
//    }

    suspend fun addUserInfoToDatabase(userDetails: UserDetails): UserDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val userFromFirebase = fireStoreDb.addUserInfoToDatabase(userDetails)
                userFromFirebase
            } catch (e: Exception) {
                Log.e("DatabaseService", "Database add failed")
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getUserInfoFromDatabase(userDetails: UserDetails): UserDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val userFromFirebase = fireStoreDb.getUserInfoFromDatabase(userDetails)
                userFromFirebase
            } catch (e: Exception) {
                Log.e("DatabaseService", "Database user fetch failed")
                null
            }
        }
    }

    suspend fun updateProfileDetails(user: UserDetails) : Boolean{
        return withContext(Dispatchers.IO) {
            try {
                val status  = fireStoreDb.updateUserInfoFromDatabase(user)
                true
            } catch (e: Exception) {
                Log.e("DatabaseLayer", "update error")
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun getUserDataFromId(uid: String): UserDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val user = fireStoreDb.getUserInfoFromId(uid)
                user
            } catch (e: Exception) {
                Log.e("DatabaseLayer","fetch failed")
                null
            }
        }
    }

    fun getUserListFromDb(user: UserDetails) : Flow<ArrayList<UserDetails>?> {
        return fireStoreDb.getAllUsersFromDb(user)
    }

    suspend fun sendNewMessage(currentUser: UserDetails, foreignUser: UserDetails,
                               messageWrapper: MessageWrapper): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val resultStatus = fireStoreDb.sendMessage(currentUser, foreignUser, messageWrapper)
                resultStatus
            } catch (e: Exception) {
                Log.e("DatabaseLayer", "Message could not be sent")
                false
            }
        }
    }

    suspend fun getAllChatsOfUser(uid: String) : ArrayList<Chat>? {
        return withContext(Dispatchers.IO) {
            try {
                var chatList = fireStoreDb.getAllChatsFromDb(uid)
                chatList
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun createChatId(currentUser: UserDetails, foreignUser: UserDetails) : Boolean{
        return withContext(Dispatchers.IO) {
            try {
                fireStoreDb.createParticipantsArray(currentUser, foreignUser)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getMessageList(currentUser: UserDetails, foreignUser: UserDetails): Flow<ArrayList<Message>?> {
        return fireStoreDb.getMessages(currentUser, foreignUser)
    }

    fun getAllGroupsOfUser(currentUser: UserDetails): Flow<ArrayList<GroupInfo>?> {
        return fireStoreDb.getAllGroupsOfUser(currentUser)
    }

    suspend fun createGroup(participants: ArrayList<String>, groupName: String) : Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result = fireStoreDb.createGroupChannel(participants, groupName)
                result
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DatabaseLayer", "create group exception")
                false
            }
        }
    }

    suspend fun sendNewMessageToGroup(currentUser: UserDetails, selectedGroup: GroupInfo, messageWrapper: MessageWrapper): Boolean {
        return  withContext(Dispatchers.IO) {
            try {
                val resultStatus = fireStoreDb.sendNewMessageToGroup(currentUser, selectedGroup, messageWrapper)
                resultStatus
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    fun getMessageListOfGroup(group: GroupInfo): Flow<ArrayList<Message>?> {
        return fireStoreDb.getAllMessagesOfGroup(group)
    }

    suspend fun getUsersInfoFromParticipants(userIdList: ArrayList<String>) : ArrayList<UserDetails> {
        return withContext(Dispatchers.IO) {
            val userList = ArrayList<UserDetails>()
            for(id in userIdList) {
                try {
                    val user = fireStoreDb.getUserInfoFromId(id)
                    userList.add(user)
                } catch(e: Exception) {
                    Log.e("DatabaseLayer", "error while fetching user")
                    e.printStackTrace()
                }
            }
            userList
        }
    }

    suspend fun uploadChatImageToCloud(imageUri: Uri, currentUser: UserDetails, foreignUser: UserDetails): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val chatId = Utilities.createChatId(currentUser.uid, foreignUser.uid)
                val url = firebaseStorage.uploadImageToCloud(imageUri, FIREBASE_CHAT_IMAGES, chatId)
                val cal = Calendar.getInstance()
                val time = cal.timeInMillis
                val messageWrapper = MessageWrapper(url, time, IMAGE)
                val resutStatus = fireStoreDb.sendMessage(currentUser, foreignUser, messageWrapper)
                resutStatus
            } catch (e: Exception) {
                Log.e("DatabaseLayer", "upload image failed exception")
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun uploadGroupImageToCloud(imageUri: Uri, selectedGroup: GroupInfo, currentUser: UserDetails): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val groupId = selectedGroup.groupId
                val url = firebaseStorage.uploadImageToCloud(imageUri, FIREBASE_GROUP_CHAT_IMAGES, groupId)
                val cal = Calendar.getInstance()
                val time = cal.timeInMillis
                val messageWrapper = MessageWrapper(url, time, IMAGE)
                val resulStatus = fireStoreDb.sendNewMessageToGroup(currentUser, selectedGroup, messageWrapper)
                resulStatus
            } catch (e: Exception) {
                Log.e("DatabaseLayer", "upload image failed exception")
                e.printStackTrace()
                false
            }
        }
    }
}