package com.bl.chatapp.data.datalayer

import android.net.Uri
import android.util.Log
import com.bl.chatapp.authservice.FirebaseAuthentication
import com.bl.chatapp.common.Constants.FIREBASE_CHAT_IMAGES
import com.bl.chatapp.common.Constants.FIREBASE_GROUP_CHAT_IMAGES
import com.bl.chatapp.common.Constants.FIREBASE_TOKEN
import com.bl.chatapp.common.Constants.IMAGE
import com.bl.chatapp.common.Utilities
import com.bl.chatapp.data.models.Chat
import com.bl.chatapp.data.models.GroupInfo
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.networking.retrofit.PushContent
import com.bl.chatapp.networking.retrofit.PushMessage
import com.bl.chatapp.networking.retrofit.PushNotificationSenderService
import com.bl.chatapp.firebase.FirebaseCloudMessagingService
import com.bl.chatapp.firebase.FirebaseDatabaseService
import com.bl.chatapp.firebase.FirebaseStorage
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
    private var pushNotificationSenderService = PushNotificationSenderService()
    private var firebaseAuth = FirebaseAuthentication()

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

    suspend fun updateUserTokenInFirestore(userDetails: UserDetails, token: String) : UserDetails? {
        return withContext(Dispatchers.IO) {
            try {
                userDetails.firebaseTokenId = token
                val user = fireStoreDb.updateUserInfoFromDatabase(userDetails)
                user
            } catch (e: Exception) {
                Log.e("DatabaseLayer", "update firebase user token failed")
                e.printStackTrace()
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
                               messageWrapper: MessageWrapper): Message? {
        return withContext(Dispatchers.IO) {
            try {
                val resultStatus = fireStoreDb.sendMessage(currentUser, foreignUser, messageWrapper)
                resultStatus
            } catch (e: Exception) {
                Log.e("DatabaseLayer", "Message could not be sent")
                null
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

    suspend fun createGroup(participants: ArrayList<String>, groupName: String, groupImageUrl: String) : Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result = fireStoreDb.createGroupChannel(participants, groupName, groupImageUrl)
                result
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DatabaseLayer", "create group exception")
                false
            }
        }
    }

    suspend fun sendNewMessageToGroup(currentUser: UserDetails, selectedGroup: GroupInfo, messageWrapper: MessageWrapper):Message? {
        return  withContext(Dispatchers.IO) {
            try {
                val resultMessage = fireStoreDb.sendNewMessageToGroup(currentUser, selectedGroup, messageWrapper)
                resultMessage
            } catch (e: Exception) {
                e.printStackTrace()
                null
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
            Log.i("DatabaseLayer","userList -> $userList")
            userList
        }
    }

    suspend fun uploadChatImageToCloud(imageUri: Uri, currentUser: UserDetails, foreignUser: UserDetails): Message? {
        return withContext(Dispatchers.IO) {
            try {
                val chatId = Utilities.createChatId(currentUser.uid, foreignUser.uid)
                val url = firebaseStorage.uploadImageToCloud(imageUri, FIREBASE_CHAT_IMAGES, chatId)
                val cal = Calendar.getInstance()
                val time = cal.timeInMillis
                val messageWrapper = MessageWrapper(url, time, IMAGE)
                val resultMessage = fireStoreDb.sendMessage(currentUser, foreignUser, messageWrapper)
                resultMessage
            } catch (e: Exception) {
                Log.e("DatabaseLayer", "upload image failed exception")
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun uploadGroupImageToCloud(imageUri: Uri, selectedGroup: GroupInfo, currentUser: UserDetails): Message? {
        return withContext(Dispatchers.IO) {
            try {
                val groupId = selectedGroup.groupId
                val url = firebaseStorage.uploadImageToCloud(imageUri, FIREBASE_GROUP_CHAT_IMAGES, groupId)
                val cal = Calendar.getInstance()
                val time = cal.timeInMillis
                val messageWrapper = MessageWrapper(url, time, IMAGE)
                val resultMessage = fireStoreDb.sendNewMessageToGroup(currentUser, selectedGroup, messageWrapper)
                resultMessage
            } catch (e: Exception) {
                Log.e("DatabaseLayer", "upload image failed exception")
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun sendNotificationToUser(userToken: String, title: String, message: String, imageUrl: String) {
        return withContext(Dispatchers.IO) {
            try {
                val pushContent = PushContent(title = title, body = message, image = imageUrl)
                val pushMessage = PushMessage(to = userToken, notification = pushContent)
                val response = pushNotificationSenderService.sendNotification(pushMessage)
                Log.i("DatabaseLayer", "response -> $response")
                Log.i("DatabaseLayer","notification sent to the user")
            } catch (e: Exception) {
                Log.e("DatabaseLayer", "push notification failed exception")
                e.printStackTrace()
            }
        }
    }

    suspend fun sendNotificationToGroup(membersTokenList: ArrayList<String>, title: String, message: String, imageUrl: String) {
        return withContext(Dispatchers.IO) {
            membersTokenList.forEach { memberToken ->
                Log.i("Database layer", "called")
                sendNotificationToUser(memberToken, title, message, imageUrl)
            }
        }
    }

    suspend fun setGroupImageInCloud(imageUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val resultUrl = firebaseStorage.setGroupImage(imageUri)
                resultUrl
            } catch (e: Exception) {
                Log.e("DatabaseLayer", "upload image failed exception")
                e.printStackTrace()
                null
            }

        }
    }

    suspend fun logOutFromTheApp(uid: String) {
        return withContext(Dispatchers.IO) {
            try {
                val map = mapOf(
                    FIREBASE_TOKEN to ""
                )
                fireStoreDb.updateFieldsOfDocument(uid, map)
                firebaseAuth.logOutFromApp()
            } catch (e: Exception) {
                Log.e("DatabaseLayer","update field exception")
                e.printStackTrace()
            }
        }
    }
}