package com.bl.chatapp.data.models

data class FirebaseUser(
    var userName: String,
    var status: String,
    var phoneNumber: String,
    var profileImageUrl: String = ""
)
