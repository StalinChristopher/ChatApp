package com.bl.chatapp.common

import com.bl.chatapp.data.models.FirebaseUser
import java.util.HashMap

object Utilities {
    fun createUserFromHashMap(userMap: HashMap<*, *>): FirebaseUser {
        return FirebaseUser(
            userMap["userName"].toString(),
            userMap["status"].toString(),
            userMap["phoneNumber"].toString()
        )
    }
}