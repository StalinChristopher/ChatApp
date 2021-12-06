package com.bl.chatapp.viewmodels

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.chatapp.authservice.FirebaseAuthentication
import com.bl.chatapp.wrappers.UserDetails
import com.google.firebase.auth.PhoneAuthProvider

class LoginViewModel(private val context: Context) : ViewModel() {
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var phoneNumber: String? = null

    private val _gotoHomePageStatus = MutableLiveData<UserDetails>()
    val gotoHomePageStatus = _gotoHomePageStatus as LiveData<UserDetails>

    private val _gotoOtpVerificationPageStatus = MutableLiveData<Boolean>()
    val gotoOtpVerificationPageStatus = _gotoOtpVerificationPageStatus as LiveData<Boolean>

    private val _invalidPhoneNumberStatus = MutableLiveData<Boolean>()
    val invalidPhoneNumberStatus = _invalidPhoneNumberStatus as LiveData<Boolean>

    private val firebaseAuth = FirebaseAuthentication()

    fun loginWithPhoneNumber(activity: Activity, mobileNumber: String, firstTime: Boolean) {
        phoneNumber = mobileNumber
        if (firstTime) {
            firebaseAuth.login(
                activity,
                mobileNumber,
                null
            ) { credential, exception, verificationIdToken ->
                if (verificationIdToken != null) {
                    verificationId = verificationIdToken.verificationId
                    resendToken = verificationIdToken.token
                    _gotoOtpVerificationPageStatus.value = true
                }
                if (exception != null) {
                    Log.i("ViewModel", "exception")
                    _invalidPhoneNumberStatus.value = true
                }
                if (credential != null) {
                    firebaseAuth.directSignIn(credential, mobileNumber) {
                        if (it != null) {
                            _gotoHomePageStatus.value = it
                        } else {
                            Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            firebaseAuth.login(
                activity,
                mobileNumber,
                resendToken
            ) { credential, exception, verificationIdToken ->
                if (verificationIdToken != null) {
                    verificationId = verificationIdToken.verificationId
                    resendToken = verificationIdToken.token
                    _gotoOtpVerificationPageStatus.value = true
                }
                if (exception != null) {
                    Log.i("ViewModel", "exception")
                    _invalidPhoneNumberStatus.value = true
                }
                if (credential != null) {
                    firebaseAuth.directSignIn(credential, mobileNumber) {
                        if (it != null) {
                            _gotoHomePageStatus.value = it
                        } else {
                            Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    fun verifyOtp(context: Context, otp: String, number: String) {
        firebaseAuth.otpVerification(verificationId, otp, number) {
            if (it != null) {
                _gotoHomePageStatus.value = it
            } else {
                Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun logOut(context: Context) {
        firebaseAuth.logOutFromApp(context)
    }


}