package com.bl.chatapp.data.services

import android.content.Context
import android.util.Log
import com.bl.chatapp.common.Constants.FIREBASE_CHATS_COLLECTION
import com.bl.chatapp.common.Constants.FIREBASE_MESSAGES_COLLECTION
import com.bl.chatapp.common.Constants.FIREBASE_PHONE
import com.bl.chatapp.common.Constants.FIREBASE_PROFILE_IMAGE_URL
import com.bl.chatapp.common.Constants.FIREBASE_STATUS
import com.bl.chatapp.common.Constants.FIREBASE_USERNAME
import com.bl.chatapp.common.Constants.FIREBASE_USERS_COLLECTION
import com.bl.chatapp.common.Constants.MESSAGE_ID
import com.bl.chatapp.common.Constants.MESSAGE_TEXT
import com.bl.chatapp.common.Constants.MESSAGE_TYPE
import com.bl.chatapp.common.Constants.PARTICIPANTS
import com.bl.chatapp.common.Constants.RECEIVER_ID
import com.bl.chatapp.common.Constants.SENDER_ID
import com.bl.chatapp.common.Constants.SENT_TIME
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.common.Utilities
import com.bl.chatapp.data.models.Chat
import com.bl.chatapp.data.models.FirebaseUser
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.wrappers.MessageWrapper
import com.bl.chatapp.wrappers.UserDetails
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import org.w3c.dom.DocumentType
import kotlin.coroutines.suspendCoroutine

class FirebaseDatabaseService {
    private var db = Firebase.firestore

    companion object {
        const val TAG = "FirebaseDatabaseService"
    }

    suspend fun addUserInfoToDatabase(userDetails: UserDetails): UserDetails {
        return suspendCoroutine { callback ->
            val dbUser = FirebaseUser(userDetails.userName, userDetails.status, userDetails.phone)
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
                                    phone = userFromDb.phoneNumber,
                                    profileImageUrl = userFromDb.profileImageUrl
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

    suspend fun updateUserInfoFromDatabase(user: UserDetails): Boolean {
        return suspendCoroutine { callback ->
            val fuId = user.uid
            var firebaseUser = FirebaseUser(
                userName = user.userName, status = user.status,
                phoneNumber = user.phone, profileImageUrl = user.profileImageUrl
            )
            val userMap = mapOf(
                FIREBASE_USERNAME to user.userName,
                FIREBASE_STATUS to user.status,
                FIREBASE_PHONE to user.phone,
                FIREBASE_PROFILE_IMAGE_URL to user.profileImageUrl
            )
            db.collection(FIREBASE_USERS_COLLECTION).document(fuId).update(userMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback.resumeWith(Result.success(true))
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
                                    profileImageUrl = userFromDb.profileImageUrl
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

    suspend fun getUserListFromDb(user: UserDetails): ArrayList<UserDetails> {
        return suspendCoroutine { callback ->
            db.collection(FIREBASE_USERS_COLLECTION).get().addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    var userList = ArrayList<UserDetails>()
                    val dataSnapshot = task.result
                    if (dataSnapshot != null) {
                        for (item in dataSnapshot.documents) {
                            if (user.uid == item.id) {
                                continue
                            } else {
                                val userMap = item.data as HashMap<*, *>
                                val userName = userMap[FIREBASE_USERNAME].toString()
                                val status = userMap[FIREBASE_STATUS].toString()
                                val phone = userMap[FIREBASE_PHONE].toString()
                                val profileImageUrl = userMap[FIREBASE_PROFILE_IMAGE_URL].toString()
                                val uid = item.id
                                val userFromDb = UserDetails(
                                    userName = userName, status = status, phone = phone,
                                    profileImageUrl = profileImageUrl, uid = uid
                                )
                                userList.add(userFromDb)
                            }
                        }
                        callback.resumeWith(Result.success(userList))
                    } else {
                        Log.e(TAG, "No users present")
                        callback.resumeWith((Result.failure(task.exception!!)))
                    }
                } else {
                    Log.i(TAG, "Users could not be fetched")
                    callback.resumeWith(Result.failure(task.exception!!))
                }
            }
        }
    }

    suspend fun sendMessage(
        currentUser: UserDetails,
        foreignUser: UserDetails,
        message: MessageWrapper
    ): Boolean {
        return suspendCoroutine { callback ->
            var chatId = ""
            if (currentUser.uid.compareTo(foreignUser.uid) > 0) {
                chatId = "${currentUser.uid}_${foreignUser.uid}"
            } else {
                chatId = "${foreignUser.uid}_${currentUser.uid}"
            }
            var autoId = db.collection(FIREBASE_CHATS_COLLECTION).document(chatId).collection(
                FIREBASE_MESSAGES_COLLECTION
            ).document().id
            var firebaseMessage = Message(
                messageId = autoId,
                senderId = currentUser.uid,
                receiverId = foreignUser.uid,
                sentTime = message.messageTime,
                messageText = message.content,
                messageType = message.type
            )
            db.collection(FIREBASE_CHATS_COLLECTION).document(chatId).collection(
                FIREBASE_MESSAGES_COLLECTION
            ).document(autoId).set(firebaseMessage).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i(TAG, "Message added successfully")
                    callback.resumeWith(Result.success(true))
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
            var chatId = ""
            chatId = if (currentUser.uid.compareTo(foreignUser.uid) > 0) {
                "${currentUser.uid}_${foreignUser.uid}"
            } else {
                "${foreignUser.uid}_${currentUser.uid}"
            }
            db.collection(FIREBASE_CHATS_COLLECTION).document(chatId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val snapshot = task.result
                        if (snapshot != null) {
                            var data = snapshot.data
                            if (data == null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    var participants = ArrayList<String>()
                                    participants.add(currentUser.uid)
                                    participants.add(foreignUser.uid)
                                    var map = mapOf(
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

//    suspend fun getSnapShotMessage(currentUser: UserDetails, foreignUser: UserDetails): ArrayList<Message> {
//        return suspendCoroutine {  callback ->
//            var chatId = ""
//            chatId = if(currentUser.uid.compareTo(foreignUser.uid) > 0) {
//                "${currentUser.uid}_${foreignUser.uid}"
//            } else {
//                "${foreignUser.uid}_${currentUser.uid}"
//            }
//            db.collection(FIREBASE_CHATS_COLLECTION).document(chatId).collection("Messages").orderBy(
//                SENT_TIME, Query.Direction.DESCENDING).addSnapshotListener { value, error ->
//                if(error is FirebaseFirestoreException) {
//                    Log.i("FirebaseDatabase","snapshotListener exception")
//                    callback.resumeWith(Result.failure(error))
//                }
//                if(value != null) {
//                    val documentChanges = value.documentChanges
//                    val messageList = ArrayList<Message>()
//                    for( doc in documentChanges ) {
//                        if(doc.type == DocumentChange.Type.ADDED) {
//                            var message = Message(
//                                messageId = doc.document.getString(MESSAGE_ID).toString(),
//                                senderId = doc.document.getString(SENDER_ID).toString(),
//                                receiverId = doc.document.getString(RECEIVER_ID).toString(),
//                                sentTime = doc.document.getLong(SENT_TIME)!!,
//                                messageText = doc.document.getString(MESSAGE_TEXT).toString(),
//                                messageType = doc.document.getString(MESSAGE_TYPE).toString())
//                            messageList.add(message)
//                        }
//                    }
//                    callback.resumeWith(Result.success(messageList))
//                }
//            }
//        }
//    }

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
                                msg.getString(
                                    RECEIVER_ID
                                )!!,
                                msg.getLong(SENT_TIME)!!,
                                msg.getString(MESSAGE_TEXT)!!,
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

}