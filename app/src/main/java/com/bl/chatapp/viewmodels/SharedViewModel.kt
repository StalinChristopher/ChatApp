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

    fun updateUserProfileDetails(currentUser: UserDetails, latestUserName: String, latestStatus: String, imageUri: Uri?) {
        viewModelScope.launch {
            var profileImageUrl : String? = null
            if(imageUri != null) {
                profileImageUrl = databaseLayer.setProfileImageInCloud(imageUri, currentUser.uid)
            }
            val result = databaseLayer.updateProfileDetails(latestUserName, latestStatus,
                currentUser.uid, profileImageUrl?: currentUser.profileImageUrl)
            _updateUserStatusStatus.postValue(result)
        }
    }

    fun logOut(uid: String) {
        viewModelScope.launch {
            databaseLayer.logOutFromTheApp(uid)
        }
    }
}