package com.bl.chatapp.wrappers

import com.bl.chatapp.data.models.Message

data class MessageWrapper(
    val content: String,
    val messageTime: Long,
    val type: String
)
