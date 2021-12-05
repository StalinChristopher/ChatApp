package com.bl.chatapp.wrappers

import java.io.Serializable

data class UserDetails(
    var uid: String,
    var newUser: Boolean = false,
    var userName: String = "",
    var status: String = "",
    var phone: String = ""
) : Serializable
