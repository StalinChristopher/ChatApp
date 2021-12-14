package com.bl.chatapp.networking.retrofit

data class PushMessage(
    var to: String,
    var notification: PushContent
)
