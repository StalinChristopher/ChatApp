package com.bl.chatapp.data.models

import com.bl.chatapp.wrappers.UserDetails

data class Chat(
    val participants: ArrayList<String>,
    val messageList: ArrayList<Message>
)
