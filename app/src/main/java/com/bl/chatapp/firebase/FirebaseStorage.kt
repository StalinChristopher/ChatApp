package com.bl.chatapp.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import com.bl.chatapp.common.Constants.FIREBASE_CHAT_IMAGES
import com.bl.chatapp.common.Constants.FIREBASE_GROUP_PROFILE_IMAGE
import com.bl.chatapp.common.Constants.FIREBASE_PROFILE_IMAGES_
import com.bl.chatapp.common.Constants.FIREBASE_USERS_COLLECTION
import com.bl.chatapp.wrappers.UserDetails
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.coroutines.suspendCoroutine

class FirebaseStorage() {
    private var storageRef = Firebase.storage.reference
    
    suspend fun setProfileImage(uri: Uri, uid: String): String {
        return suspendCoroutine { callback ->
            var urlImage: String
            val profileImageRef =
                storageRef.child(FIREBASE_PROFILE_IMAGES_).child(FIREBASE_USERS_COLLECTION)
                    .child("$uid.webp")
            profileImageRef.putFile(uri).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("Storage", "Upload image complete")
                    profileImageRef.downloadUrl.addOnSuccessListener { task ->
                        urlImage = task.toString()
                        callback.resumeWith(Result.success(urlImage))
                    }
                    Log.i("Storage", "url fetch failed")
                } else {
                    Log.i("Storage", "Upload failed")
                    callback.resumeWith(Result.failure(it.exception!!))
                }
            }
        }
    }

    suspend fun uploadImageToCloud(uri: Uri, collectionType: String, conversationId: String) : String {
        return suspendCoroutine { callback ->
            val imageId = UUID.randomUUID().toString()
            var urlString: String
            var imageRef = storageRef.child(collectionType).child(conversationId).child("$imageId.webp")
            imageRef.putFile(uri).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("Storage", "Upload image complete")
                    imageRef.downloadUrl.addOnSuccessListener { task ->
                        urlString = task.toString()
                        Log.i("Storage", urlString)
                        callback.resumeWith(Result.success(urlString))
                    }
                    Log.i("Storage", "url fetch failed")
                } else {
                    Log.i("Storage", "Upload failed")
                    callback.resumeWith(Result.failure(it.exception!!))
                }
            }
        }
    }

    suspend fun setGroupImage(uri: Uri) : String {
        return suspendCoroutine { callback ->
            var urlImage: String
            val imageId = UUID.randomUUID()
            val profileImageRef =
                storageRef.child(FIREBASE_GROUP_PROFILE_IMAGE).child("${imageId}.webp")
            profileImageRef.putFile(uri).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("Storage", "Upload image complete")
                    profileImageRef.downloadUrl.addOnSuccessListener { task ->
                        urlImage = task.toString()
                        callback.resumeWith(Result.success(urlImage))
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