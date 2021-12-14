package com.bl.chatapp.firebase

import android.util.Log
import com.bl.chatapp.common.Constants.FIREBASE_CHATS_COLLECTION
import com.bl.chatapp.common.Constants.FIREBASE_GROUP_CHATS_COLLECTION
import com.bl.chatapp.common.Constants.FIREBASE_MESSAGES_COLLECTION
import com.bl.chatapp.common.Constants.FIREBASE_PHONE
import com.bl.chatapp.common.Constants.FIREBASE_PROFILE_IMAGE_URL
import com.bl.chatapp.common.Constants.FIREBASE_STATUS
import com.bl.chatapp.common.Constants.FIREBASE_USERNAME
import com.bl.chatapp.common.Constants.FIREBASE_USERS_COLLECTION
import com.bl.chatapp.common.Constants.MESSAGE_ID
import com.bl.chatapp.common.Constants.CONTENT
import com.bl.chatapp.common.Constants.FIREBASE_TOKEN
import com.bl.chatapp.common.Constants.MESSAGE_TYPE
import com.bl.chatapp.common.Constants.PARTICIPANTS
import com.bl.chatapp.common.Constants.SENDER_ID
import com.bl.chatapp.common.Constants.SENT_TIME
import com.bl.chatapp.common.Utilities
import com.bl.chatapp.data.models.Chat
import com.bl.chatapp.data.models.FirebaseUser
import com.bl.chatapp.data.models.GroupInfo
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.wrappers.MessageWrapper
import com.bl.chatapp.wrappers.UserDetails
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.suspendCoroutine

class FirebaseDatabaseService {
    private var db = Firebase.firestore

    companion object {
        const val TAG = "FirebaseDatabaseService"
    }

    suspend fun addUserInfoToDatabase(userDetails: UserDetails): UserDetails {
        return suspendCoroutine { callback ->
            val dbUser = FirebaseUser(userDetails.userName, userDetails.status, userDetails.phone, firebaseToken = userDetails.firebaseTokenId)
            db.collection(FIREBASE_USERS_COLLECTION).document(userDetails.uid).set(dbUser)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback.resumeWith(Result.success(userDetails))
                        Log.i(TAG, "user added")
                    } else {
                        callback.resumeWith(Result.failure(it.exception!!))
                    }
                }
        }
    }

    suspend fun getUserInfoFromDatabase(userDetails: UserDetails): UserDetails {
        return suspendCoroutine { callback ->
            val fuId = userDetails.uid
            db.collection(FIREBASE_USERS_COLLECTION).document(fuId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.also {
                            if (it.data != null) {
                                val userMap = it.data as HashMap<*, *>
                                val userFromDb = Utilities.createUserFromHashMap(userMap)
                                val user = UserDetails(
                                    uid = fuId,
                                    userName = userFromDb.userName,
                                    status = userFromDb.status,
                                    phone = userDetails.phone,
                                    profileImageUrl = userFromDb.profileImageUrl,
                                    firebaseTokenId = userFromDb.firebaseToken
                                )
                                callback.resumeWith(Result.success(user))
                            } else {
                                callback.resumeWith(Result.failure(task.exception!!))
                            }
                        }
                    } else {
                        Log.i(TAG, "read user failed")
                        callback.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    suspend fun updateUserInfoFromDatabase(user: UserDetails): UserDetails {
        return suspendCoroutine { callback ->
            val fuId = user.uid
            val userMap = mapOf(
                FIREBASE_USERNAME to user.userName,
                FIREBASE_STATUS to user.status,
                FIREBASE_PHONE to user.phone,
                FIREBASE_PROFILE_IMAGE_URL to user.profileImageUrl,
                FIREBASE_TOKEN to user.firebaseTokenId
            )
            db.collection(FIREBASE_USERS_COLLECTION).document(fuId).update(userMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback.resumeWith(Result.success(user))
                    } else {
                        Log.e(TAG, "Update user failed")
                        callback.resumeWith(Result.failure(it.exception!!))
                    }
                }
        }
    }

    suspend fun getUserInfoFromId(uid: String): UserDetails {
        return suspendCoroutine { callback ->
            db.collection(FIREBASE_USERS_COLLECTION).document(uid).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.also {
                            if (it.data != null) {
                                val userMap = it.data as HashMap<*, *>
                                val userFromDb = Utilities.createUserFromHashMap(userMap)
                                val user = UserDetails(
                                    uid = uid,
                                    userName = userFromDb.userName,
                                    status = userFromDb.status,
                                    phone = userFromDb.phoneNumber,
                                    profileImageUrl = userFromDb.profileImageUrl,
                                    firebaseTokenId = userFromDb.firebaseToken
                                )
                                callback.resumeWith(Result.success(user))
                            } else {
                                callback.resumeWith(Result.failure(task.exception!!))
                            }
                        }
                    } else {
                        Log.i(TAG, "read user failed")
                        callback.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    suspend fun sendMessage(
        currentUser: UserDetails,
        foreignUser: UserDetails,
        message: MessageWrapper
    ): Message {
        return suspendCoroutine { callback ->
            val chatId = Utilities.createChatId(currentUser.uid, foreignUser.uid)
            val autoId = db.collection(FIREBASE_CHATS_COLLECTION).document(chatId).collection(
                FIREBASE_MESSAGES_COLLECTION
            ).document().id
            val firebaseMessage = Message(
                messageId = autoId,
                senderId = currentUser.uid,
                sentTime = message.messageTime,
                content = message.content,
                messageType = message.type
            )
            db.collection(FIREBASE_CHATS_COLLECTION).document(chatId).collection(
                FIREBASE_MESSAGES_COLLECTION
            ).document(autoId).set(firebaseMessage).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i(TAG, "Message added successfully")
                    callback.resumeWith(Result.success(firebaseMessage))
                } else {
                    Log.i(TAG, "Message add failed")
                    callback.resumeWith(Result.failure(it.exception!!))
                }
            }
        }
    }

    suspend fun createParticipantsArray(
        currentUser: UserDetails,
        foreignUser: UserDetails
    ): Boolean {
        return suspendCoroutine { callback ->
            val chatId = Utilities.createChatId(currentUser.uid, foreignUser.uid)
            db.collection(FIREBASE_CHATS_COLLECTION).document(chatId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val snapshot = task.result
                        if (snapshot != null) {
                            val data = snapshot.data
                            if (data == null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val participants = ArrayList<String>()
                                    participants.add(currentUser.uid)
                                    participants.add(foreignUser.uid)
                                    val map = mapOf(
                                        PARTICIPANTS to participants
                                    )
                                    db.collection(FIREBASE_CHATS_COLLECTION).document(chatId)
                                        .set(map).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            callback.resumeWith(Result.success(true))
                                        } else {
                                            callback.resumeWith(Result.failure(it.exception!!))
                                        }
                                    }
                                }
                            } else {
                                callback.resumeWith(Result.success(true))
                            }
                        }
                    }
                }
        }
    }

    suspend fun getAllChatsFromDb(uid: String): ArrayList<Chat> {
        return suspendCoroutine { callback ->
            db.collection(FIREBASE_CHATS_COLLECTION).whereArrayContains(PARTICIPANTS, uid).get()
                .addOnSuccessListener { task ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val requests = ArrayList<Deferred<Chat>>()
                        for (doc in task) {
                            requests.add(async { getMessages(doc) })
                        }
                        val chats = requests.awaitAll() as ArrayList<Chat>
                        callback.resumeWith(Result.success(chats))
                        Log.i(TAG, "size -> ${chats.size}")
                    }
                }
                .addOnFailureListener {
                    callback.resumeWith(Result.failure(it))
                }
        }
    }

    private suspend fun getMessages(doc: QueryDocumentSnapshot) =
        suspendCoroutine<Chat> { callback ->
            doc.reference.collection(FIREBASE_MESSAGES_COLLECTION)
                .orderBy(SENT_TIME, Query.Direction.DESCENDING)
                .get().addOnSuccessListener { snapshot ->
                    val messageList = ArrayList<Message>()
                    for (msg in snapshot.documents) {
                        messageList.add(
                            Message(
                                msg.getString(MESSAGE_ID)!!,
                                msg.getString(SENDER_ID)!!,
                                msg.getLong(SENT_TIME)!!,
                                msg.getString(CONTENT)!!,
                                msg.getString(MESSAGE_TYPE)!!
                            )
                        )
                    }
                    val chat = Chat(doc.get(PARTICIPANTS) as ArrayList<String>, messageList)
                    Log.i(TAG, "chat -> $chat")
                    callback.resumeWith(Result.success(chat))
                }
                .addOnFailureListener {
                    callback.resumeWith(Result.failure(it))
                    Log.e(TAG, "particular set of chat messages fetch failed")
                }
        }

    @ExperimentalCoroutinesApi
    fun getMessages(currentUser: UserDetails, foreignUser: UserDetails) : Flow<ArrayList<Message>?> {
        return callbackFlow {
            val chatId = Utilities.createChatId(currentUser.uid, foreignUser.uid)
            val listenerRef = db.collection(FIREBASE_CHATS_COLLECTION).document(chatId)
                .collection(FIREBASE_MESSAGES_COLLECTION).orderBy(SENT_TIME, Query.Direction.DESCENDING)
                .limit(10).addSnapshotListener { snapshot, error ->
                if(error != null) {
                    this.trySend(null)
                    Log.e(TAG, "snapshot listener error")
                    error.printStackTrace()
                } else {
                    if(snapshot != null) {
                        val messageList = ArrayList<Message>()
                        for(doc in snapshot.documents) {
                            val data = doc.data as HashMap<*, *>
                                val message = Message(
                                    data[MESSAGE_ID].toString(),
                                    data[SENDER_ID].toString(),
                                    data[SENT_TIME] as Long,
                                    data[CONTENT].toString(),
                                    data[MESSAGE_TYPE].toString()
                                )
                                messageList.add(message)
                        }
                        this.trySend(messageList)
                    } else {
                        Log.e(TAG,"snapshot is null error")
                    }
                }
            }
            awaitClose { listenerRef.remove() }
        }
    }

    fun getAllUsersFromDb(user: UserDetails) : Flow<ArrayList<UserDetails>?> {
        return callbackFlow {
            val userList = ArrayList<UserDetails>()
            val listenerRef = db.collection(FIREBASE_USERS_COLLECTION).addSnapshotListener { snapshot, error ->
                if(error != null) {
                    this.offer(null)
                    Log.e(TAG, "snapshot listener error")
                    error.printStackTrace()
                } else {
                    if(snapshot != null) {
                        for(doc in snapshot.documentChanges) {
                            if(doc.type == DocumentChange.Type.ADDED) {
                                val item = doc.document
                                if(item.id == user.uid) {
                                    continue
                                } else {
                                    val userMap = item.data as HashMap<*, *>
                                    val userName = userMap[FIREBASE_USERNAME].toString()
                                    val status = userMap[FIREBASE_STATUS].toString()
                                    val phone = userMap[FIREBASE_PHONE].toString()
                                    val profileImageUrl = userMap[FIREBASE_PROFILE_IMAGE_URL].toString()
                                    val userToken = userMap[FIREBASE_TOKEN].toString()
                                    val uid = item.id
                                    val userFromDb = UserDetails(
                                        userName = userName, status = status, phone = phone,
                                        profileImageUrl = profileImageUrl, uid = uid, firebaseTokenId = userToken
                                    )
                                    userList.add(userFromDb)
                                }
                            }
                        }
                        this.offer(userList)
                    } else {
                        Log.e(TAG,"snapshot is null error")
                    }
                }
            }
            awaitClose { listenerRef.remove() }
        }
    }

    suspend fun createGroupChannel(participants: ArrayList<String>, groupName: String, groupImageUrl: String) : Boolean {
        return suspendCoroutine { callback ->
            val autoId = db.collection(FIREBASE_GROUP_CHATS_COLLECTION).document().id
            val map = mapOf(
                "groupId" to autoId,
                "groupName" to groupName,
                "groupImageUrl" to groupImageUrl,
                "participants" to participants
            )
            db.collection(FIREBASE_GROUP_CHATS_COLLECTION).document(autoId).set(map).addOnCompleteListener {
                if(it.isSuccessful) {
                    Log.i(TAG, "create group successful")
                    callback.resumeWith(Result.success(true))
                } else {
                    Log.i(TAG, "Create group failed")
                    callback.resumeWith(Result.failure(it.exception!!))
                }
            }
        }
    }

    fun getAllGroupsOfUser(currentUser: UserDetails) : Flow<ArrayList<GroupInfo>?> {
        return callbackFlow {
            val groupList = ArrayList<GroupInfo>()
            val listenerRef = db.collection(FIREBASE_GROUP_CHATS_COLLECTION).
            whereArrayContains(PARTICIPANTS, currentUser.uid).addSnapshotListener { snapshot, error ->
                if(error != null) {
                    this.offer(null)
                    Log.e(TAG,"snapshot listener error")
                } else {
                    if(snapshot != null) {
                        for (doc in snapshot.documentChanges) {
                            if(doc.type == DocumentChange.Type.ADDED) {
                                val item = doc.document
                                val groupMap = item.data as HashMap<*, *>
                                val groupId = item.id
                                val participants = groupMap[PARTICIPANTS] as ArrayList<String>
                                val groupName = groupMap["groupName"].toString()
                                val groupImageUrl = groupMap["groupImageUrl"].toString()
                                val groupInfo = GroupInfo(groupId, groupName, groupImageUrl, participants)
                                groupList.add(groupInfo)
                            }
                        }
                        this.offer(groupList)
                    } else {
                        Log.e(TAG,"snapshot is null error")
                    }
                }
            }
            awaitClose { listenerRef.remove() }
        }
    }

    suspend fun sendNewMessageToGroup(currentUser: UserDetails, selectedGroup: GroupInfo,
                                      messageWrapper: MessageWrapper): Message {
        return suspendCoroutine { callback ->
            val autoId = db.collection(FIREBASE_GROUP_CHATS_COLLECTION)
                .document(selectedGroup.groupId).collection(
                FIREBASE_MESSAGES_COLLECTION
            ).document().id
            val firebaseMessage = Message(
                messageId = autoId,
                senderId = currentUser.uid,
                sentTime = messageWrapper.messageTime,
                content = messageWrapper.content,
                messageType = messageWrapper.type
            )
            db.collection(FIREBASE_GROUP_CHATS_COLLECTION).document(selectedGroup.groupId).collection(
                FIREBASE_MESSAGES_COLLECTION
            ).document(autoId).set(firebaseMessage).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i(TAG, "Group Message added successfully")
                    callback.resumeWith(Result.success(firebaseMessage))
                } else {
                    Log.i(TAG, "Group Message add failed")
                    callback.resumeWith(Result.failure(it.exception!!))
                }
            }
        }
    }

    fun getAllMessagesOfGroup(group: GroupInfo): Flow<ArrayList<Message>?> {
        return callbackFlow {
            val listenerRef = db.collection(FIREBASE_GROUP_CHATS_COLLECTION).document(group.groupId)
                .collection(FIREBASE_MESSAGES_COLLECTION).orderBy(SENT_TIME, Query.Direction.DESCENDING)
                .limit(10).addSnapshotListener { snapshot, error ->
                    if(error != null) {
                        this.trySend(null)
                        Log.e(TAG, "snapshot listener error")
                        error.printStackTrace()
                    } else {
                        if(snapshot != null) {
                            val messageList = ArrayList<Message>()
                            for(doc in snapshot.documents) {
                                val data = doc.data as HashMap<*,*>
                                val message = Message(
                                    data[MESSAGE_ID].toString(),
                                    data[SENDER_ID].toString(),
                                    data[SENT_TIME] as Long,
                                    data[CONTENT].toString(),
                                    data[MESSAGE_TYPE].toString()
                                )
                                messageList.add(message)
                            }
                            this.trySend(messageList)
                        } else {
                            Log.e(TAG,"snapshot is null error")
                        }
                    }
                }
            awaitClose { listenerRef.remove() }
        }
    }

    suspend fun updateFieldsOfDocument(uid: String, map: Map<String, *>) {
        return suspendCoroutine { callback ->
            db.collection(FIREBASE_USERS_COLLECTION).document(uid).update(map).addOnCompleteListener {
                if(it.isSuccessful) {
                    Log.i("DatabaseLayer", "updated given fields")
                    callback.resumeWith(Result.success(Unit))
                } else {
                    Log.e("DatabaseLayer", "given fields not updated")
                    callback.resumeWith(Result.failure(it.exception!!))
                }
            }
        }
    }

    suspend fun getPagedMessages(currentUser: UserDetails, foreignUser: UserDetails, offset: Long): ArrayList<Message> {
        return suspendCoroutine { callback ->
            val chatId = Utilities.createChatId(currentUser.uid, foreignUser.uid)
            db.collection(FIREBASE_CHATS_COLLECTION).document(chatId)
                .collection(
                FIREBASE_MESSAGES_COLLECTION).orderBy(SENT_TIME, Query.Direction.DESCENDING)
                .startAfter(offset).limit(10).get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val messageList = arrayListOf<Message>()
                        val snapshot = task.result
                        if(snapshot != null) {
                            for(item in snapshot.documents) {
                                val data  = item.data as HashMap<*,*>
                                val message = Message(
                                    data[MESSAGE_ID].toString(),
                                    data[SENDER_ID].toString(),
                                    data[SENT_TIME] as Long,
                                    data[CONTENT].toString(),
                                    data[MESSAGE_TYPE].toString()
                                )
                                messageList.add(message)
                            }
                            callback.resumeWith(Result.success(messageList))
                        }
                    } else {
                        callback.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    suspend fun getGroupChatPagedMessages(group: GroupInfo, offset: Long) : ArrayList<Message> {
        return suspendCoroutine { callback ->
            db.collection(FIREBASE_GROUP_CHATS_COLLECTION).document(group.groupId)
                .collection(FIREBASE_MESSAGES_COLLECTION).orderBy(SENT_TIME, Query.Direction.DESCENDING)
                .startAfter(offset).limit(10).get().addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val messageList = arrayListOf<Message>()
                        val snapshot = task.result
                        if(snapshot != null) {
                            for(item in snapshot.documents) {
                                val data  = item.data as HashMap<*,*>
                                val message = Message(
                                    data[MESSAGE_ID].toString(),
                                    data[SENDER_ID].toString(),
                                    data[SENT_TIME] as Long,
                                    data[CONTENT].toString(),
                                    data[MESSAGE_TYPE].toString()
                                )
                                messageList.add(message)
                            }
                            callback.resumeWith(Result.success(messageList))
                        }
                    } else {
                        callback.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }
}