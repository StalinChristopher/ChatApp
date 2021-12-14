package com.bl.chatapp.main

import android.app.Application
import android.util.Log
import com.bl.chatapp.common.Constants.FIREBASE_MESSAGING_TOKEN_SHAREDPREF
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.firebase.FirebaseCloudMessagingService


class ChatApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        val firebaseCloudMessagingService = FirebaseCloudMessagingService()
        firebaseCloudMessagingService.generateFirebaseMessagingToken { tokenString ->
            if(tokenString.isNotBlank()) {
                Log.i("ChatApplication", "Token added successfully to sharedPref")
                SharedPref.getInstance(this).addKeyValue(FIREBASE_MESSAGING_TOKEN_SHAREDPREF, tokenString)
                val token = SharedPref.getInstance(this).getValue(FIREBASE_MESSAGING_TOKEN_SHAREDPREF)
                Log.i("ChatApplication", "$token")
            }
        }



    }
}