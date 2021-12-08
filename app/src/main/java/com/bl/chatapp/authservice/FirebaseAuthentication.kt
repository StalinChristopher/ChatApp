package com.bl.chatapp.authservice

import android.app.Activity
import android.content.Context
import android.util.Log
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.wrappers.UserDetails
import com.bl.chatapp.wrappers.VerificationIdToken
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.PhoneAuthProvider
import java.lang.Exception


class FirebaseAuthentication {
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    fun getCurrentUser() = firebaseAuth.currentUser

    fun login(
        activity: Activity, phoneNumber: String,
        resendToken: PhoneAuthProvider.ForceResendingToken?,
        listener: (PhoneAuthCredential?, FirebaseException?, VerificationIdToken?) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                listener(credential, null, null)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                listener(null, p0, null)
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                val newVerificationTokenId = VerificationIdToken(p0, p1)
                listener(null, null, newVerificationTokenId)

            }
        }
        if (resendToken != null) {
            resendVerificationCode(activity, resendToken, phoneNumber, callbacks)
        } else {
            sendVerificationCode(activity, phoneNumber, callbacks)
        }
    }

    private fun sendVerificationCode(
        activity: Activity,
        phoneNumber: String, callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun otpVerification(verificationID: String?, otp: String, phoneNumber: String, listener: (UserDetails?) -> Unit) {
        val credential = verificationID?.let { PhoneAuthProvider.getCredential(it, otp) }
        if (credential != null) {
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("FirebaseAuthentication", "otp successfully verified")
                    val res = task.result
                    val userDetails = UserDetails(res?.user?.uid.toString())
                    userDetails.phone = phoneNumber
                    if (res?.additionalUserInfo?.isNewUser == true) {
                        Log.i("FirebaseAuth", "new user")
                        userDetails.newUser = true
                    }
                    listener(userDetails)
                } else {
                    try {
                        throw task.exception ?: Exception("Otp verification exception")
                    } catch (e: FirebaseException) {
                        if (e is FirebaseAuthInvalidCredentialsException) {
                            Log.i("FirebaseAuth", "invalid otp")
                        } else {
                            Log.i("FirebaseAuth", "Something went wrong")
                        }
                    } finally {
                        listener(null)
                    }
                }
            }
        }
    }

    fun directSignIn(credential: PhoneAuthCredential, phoneNumber: String, listener: (UserDetails?) -> Unit) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("FirebaseAuthentication", "otp successfully verified")
                val res = task.result
                val userDetails = UserDetails(res?.user?.uid.toString())
                userDetails.phone = phoneNumber
                if (res?.additionalUserInfo?.isNewUser == true) {
                    Log.i("FirebaseAuth", "new user")
                    userDetails.newUser = true
                }
                listener(userDetails)
            } else {
                try {
                    throw task.exception ?: Exception("Otp verification exception")
                } catch (e: FirebaseException) {
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        Log.i("FirebaseAuth", "invalid otp")
                    } else {
                        Log.i("FirebaseAuth", "Something went wrong")
                    }
                } finally {
                    listener(null)
                }
            }
        }
    }

    fun logOutFromApp(context: Context) {
        SharedPref.getInstance(context).clearAll()
        firebaseAuth.signOut()
    }

    private fun resendVerificationCode(
        activity: Activity,
        resendToken: PhoneAuthProvider.ForceResendingToken?,
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        Log.i("resend", "Reached")
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}