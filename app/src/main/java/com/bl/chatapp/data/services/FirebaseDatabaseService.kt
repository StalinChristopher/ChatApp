package com.bl.chatapp.data.services

import android.util.Log
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
            db.collection("users").document(userDetails.uid).set(dbUser).addOnCompleteListener {
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
            db.collection("users").document(fuId).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.also {
                        if (it.data != null) {
                            val userMap = it.data as HashMap<*, *>
                            val userFromDb = Utilities.createUserFromHashMap(userMap)
                            val user = UserDetails(
                                uid = fuId,
                                userName = userFromDb.userName,
                                status = userFromDb.status,
                                phone = userFromDb.phoneNumber
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