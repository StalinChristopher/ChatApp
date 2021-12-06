package com.bl.chatapp.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.data.services.DatabaseLayer
import com.bl.chatapp.data.services.FirebaseStorage
import com.bl.chatapp.wrappers.UserDetails
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _newUserAddStatus = MutableLiveData<UserDetails>()
    val newUserAddStatus = _newUserAddStatus as LiveData<UserDetails>

    private val _getUserInfoStatus = MutableLiveData<UserDetails>()
    val getUserInfoStatus = _getUserInfoStatus as LiveData<UserDetails>

    private val _setUserProfileImageStatus = MutableLiveData<Uri>()
    val setUserProfileImageStatus = _setUserProfileImageStatus as LiveData<Uri>

    private val _updateUserStatusStatus = MutableLiveData<Boolean>()
    val updateUserInfoStatus = _updateUserStatusStatus as LiveData<Boolean>

    private val _userInfoFromIdStatus = MutableLiveData<UserDetails>()
    val userInfoFromIdStatus = _userInfoFromIdStatus as LiveData<UserDetails>

    fun setUserData(context: Context, userName: String,
                    statusText: String, userDetails: UserDetails) {
        viewModelScope.launch {
            userDetails.userName = userName
            userDetails.status = statusText
            val user = DatabaseLayer.getInstance(context).addUserInfoToDatabase(userDetails)
            if(user != null) {
                SharedPref.getInstance(context).addUserId(userDetails.uid)
                _newUserAddStatus.postValue(user)
            }
        }
    }

    fun getUserData(context: Context, userDetails: UserDetails) {
        viewModelScope.launch {
            val user = DatabaseLayer.getInstance(context).getUserInfoFromDatabase(userDetails)
            if(user != null) {
                SharedPref.getInstance(context).addUserId(userDetails.uid)
                _getUserInfoStatus.postValue(user)
            }
        }
    }

    fun getUserInfoFromId(context: Context) {
        viewModelScope.launch {
            val uid = SharedPref.getInstance(context).getUserId()
            val user = DatabaseLayer.getInstance(context).getUserDataFromId(uid)
            if(user != null) {
                _userInfoFromIdStatus.postValue(user)
            }
        }
    }

    fun setProfileImage(context: Context, uri: Uri, userDetails: UserDetails){
        viewModelScope.launch {
            try {
                val resultUri = FirebaseStorage.getInstance(context).setProfileImage(uri, userDetails)
                _setUserProfileImageStatus.postValue(resultUri)
            } catch (e: Exception) {
                Log.i("UserViewModel","firebase storage set profile image exception")
                e.printStackTrace()
                _setUserProfileImageStatus.postValue(null)
            }

        }
    }

    fun updateUserProfileDetails(context: Context, userDetails: UserDetails) {
        viewModelScope.launch {
            val result = DatabaseLayer.getInstance(context).updateProfileDetails(userDetails)
            _updateUserStatusStatus.postValue(result)
        }
    }

}