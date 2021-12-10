package com.bl.chatapp.data.services

import android.content.Context
import android.net.Uri
import android.util.Log
import com.bl.chatapp.common.Constants.FIREBASE_PROFILE_IMAGES_
import com.bl.chatapp.common.Constants.FIREBASE_USERS_COLLECTION
import com.bl.chatapp.wrappers.UserDetails
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.coroutines.suspendCoroutine

class FirebaseStorage() {
    private var storageRef = Firebase.storage.reference
    
    suspend fun setProfileImage(uri: Uri, userDetails: UserDetails): Uri {
        return suspendCoroutine { callback ->
            var urlImage: String
            val profileImageRef =
                storageRef.child(FIREBASE_PROFILE_IMAGES_).child(FIREBASE_USERS_COLLECTION)
                    .child(userDetails.uid)
                    .child("profileImage.webp")
            profileImageRef.putFile(uri).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("Storage", "Upload image complete")
                    profileImageRef.downloadUrl.addOnSuccessListener { task ->
                        urlImage = task.toString()
                        Log.i("Storage", urlImage)
                        callback.resumeWith(Result.success(task))
                    }
                    Log.i("Storage", "url fetch failed")
                } else {
                    Log.i("Storage", "Upload failed")
                    callback.resumeWith(Result.failure(it.exception!!))
                }
            }
        }
    }
}