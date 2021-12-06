package com.bl.chatapp.common

import android.content.Context
import com.bl.chatapp.R
import com.google.android.material.textfield.TextInputEditText

object Validator {
    fun verifyUserName(context: Context, userNameEdittext: TextInputEditText) : Boolean {
        val userName = userNameEdittext.text.toString()
        var status = true
        if(userName.isEmpty()) {
            userNameEdittext.error = context.getString(R.string.username_cannot_be_empty)
            status = false
        }
        return status
    }
}