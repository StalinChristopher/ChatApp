package com.bl.chatapp.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.chatapp.data.datalayer.DatabaseLayer
import com.bl.chatapp.firebase.FirebaseStorage
import com.bl.chatapp.wrappers.UserDetails
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {
    private val databaseLayer = DatabaseLayer()
    private val firebaseStorage = FirebaseStorage()

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

    fun setUserData(userName: String,
                    statusText: String, firebaseToken :String, userDetails: UserDetails) {
        viewModelScope.launch {
            userDetails.firebaseTokenId = firebaseToken
            userDetails.userName = userName
            userDetails.status = statusText
            val user = databaseLayer.addUserInfoToDatabase(userDetails)
            if(user != null) {
                _newUserAddStatus.postValue(user)
            }
        }
    }

    fun getUserData(userDetails: UserDetails, token: String) {
        viewModelScope.launch {
            val user = databaseLayer.getUserInfoFromDatabase(userDetails)
            if(user != null) {
                val updateUser = databaseLayer.updateUserTokenInFirestore(user, token)
                if(updateUser != null) {
                    _getUserInfoStatus.postValue(updateUser)
                }
            }

        }
    }

    fun getUserInfoFromId(uid: String) {
        viewModelScope.launch {
            val user = databaseLayer.getUserDataFromId(uid)
            if(user != null) {
                _userInfoFromIdStatus.postValue(user)
            }
        }
    }

    fun setProfileImage(uri: Uri, userDetails: UserDetails){
        viewModelScope.launch {
            try {
                val resultUri = firebaseStorage.setProfileImage(uri, userDetails)
                _setUserProfileImageStatus.postValue(resultUri)
            } catch (e: Exception) {
                Log.i("UserViewModel","firebase storage set profile image exception")
                e.printStackTrace()
                _setUserProfileImageStatus.postValue(null)
            }

        }
    }

    fun updateUserProfileDetails(userDetails: UserDetails) {
        viewModelScope.launch {
            val result = databaseLayer.updateProfileDetails(userDetails)
            _updateUserStatusStatus.postValue(result)
        }
    }
}