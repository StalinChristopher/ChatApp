package com.bl.chatapp.data.services

import android.util.Log
import com.bl.chatapp.common.Constants.FIREBASE_PHONE
import com.bl.chatapp.common.Constants.FIREBASE_PROFILE_IMAGE_URL
import com.bl.chatapp.common.Constants.FIREBASE_STATUS
import com.bl.chatapp.common.Constants.FIREBASE_USERNAME
import com.bl.chatapp.common.Constants.FIREBASE_USER_COLLECTIONS
import com.bl.chatapp.common.Constants.PHONE_NUMBER
import com.bl.chatapp.common.Utilities
import com.bl.chatapp.data.models.FirebaseUser
import com.bl.chatapp.wrappers.UserDetails
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.suspendCoroutine

class FirebaseDatabaseService {
    private var db = Firebase.firestore

    suspend fun addUserInfoToDatabase(userDetails: UserDetails): UserDetails {
        return suspendCoroutine { callback ->
            val dbUser = FirebaseUser(userDetails.userName, userDetails.status, userDetails.phone)
            db.collection(FIREBASE_USER_COLLECTIONS).document(userDetails.uid).set(dbUser).addOnCompleteListener {
                if (it.isSuccessful) {
                    callback.resumeWith(Result.success(userDetails))
                    Log.i("FirebaseDatabaseService", "user added")
                } else {
                    callback.resumeWith(Result.failure(it.exception!!))
                }
            }
        }
    }

    suspend fun getUserInfoFromDatabase(userDetails: UserDetails): UserDetails {
        return suspendCoroutine { callback ->
            val fuId = userDetails.uid
            db.collection(FIREBASE_USER_COLLECTIONS).document(fuId).get().addOnCompleteListener { task ->
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
                    Log.i("FirebaseDatabaseService", "read user failed")
                    callback.resumeWith(Result.failure(task.exception!!))
                }
            }
        }
    }

    suspend fun updateUserInfoFromDatabase(user: UserDetails) : Boolean{
        return suspendCoroutine { callback ->
            val fuId = user.uid
            var firebaseUser = FirebaseUser(userName = user.userName, status = user.status,
                phoneNumber = user.phone, profileImageUrl = user.profileImageUrl)
            val userMap = mapOf(
                FIREBASE_USERNAME to user.userName,
                FIREBASE_STATUS to user.status,
                FIREBASE_PHONE to user.phone,
                FIREBASE_PROFILE_IMAGE_URL to user.profileImageUrl)
            db.collection(FIREBASE_USER_COLLECTIONS).document(fuId).update(userMap).addOnCompleteListener {
                if(it.isSuccessful) {
                    callback.resumeWith(Result.success(true))
                } else {
                    Log.e("Database", "Update user failed")
                    callback.resumeWith(Result.failure(it.exception!!))
                }
            }
        }
    }

    suspend fun getUserInfoFromId(uid: String): UserDetails {
        return suspendCoroutine { callback ->
            db.collection(FIREBASE_USER_COLLECTIONS).document(uid).get().addOnCompleteListener { task ->
                if(task.isSuccessful) {
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
                    Log.i("FirebaseDatabaseService", "read user failed")
                    callback.resumeWith(Result.failure(task.exception!!))
                }
            }
        }
    }
}