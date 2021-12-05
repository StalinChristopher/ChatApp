package com.bl.chatapp.wrappers

import com.google.firebase.auth.PhoneAuthProvider
import java.io.Serializable

data class VerificationIdToken(
    val verificationId: String,
    val token: PhoneAuthProvider.ForceResendingToken,
) : Serializable