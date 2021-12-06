package com.bl.chatapp.common

import com.bl.chatapp.common.Constants.FIREBASE_PROFILE_IMAGE_URL
import com.bl.chatapp.common.Constants.FIREBASE_STATUS
import com.bl.chatapp.common.Constants.FIREBASE_USERNAME
import com.bl.chatapp.common.Constants.PHONE_NUMBER
import com.bl.chatapp.data.models.FirebaseUser
import java.util.HashMap

object Utilities {
    fun createUserFromHashMap(userMap: HashMap<*, *>): FirebaseUser {
        return FirebaseUser(
            userMap[FIREBASE_USERNAME].toString(),
            userMap[FIREBASE_STATUS].toString(),
            userMap[PHONE_NUMBER].toString(),
            userMap[FIREBASE_PROFILE_IMAGE_URL].toString()
        )
    }
}