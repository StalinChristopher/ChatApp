package com.bl.chatapp.data.models

import java.io.Serializable

data class Message(
    val messageId: String,
    val senderId : String,
    val sentTime : Long,
    val content: String,
    val messageType: String
) : Serializable
