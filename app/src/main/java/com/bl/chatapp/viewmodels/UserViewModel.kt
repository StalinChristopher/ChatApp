package com.bl.chatapp.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.data.services.DatabaseLayer
import com.bl.chatapp.wrappers.UserDetails
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _newUserAddStatus = MutableLiveData<UserDetails>()
    val newUserAddStatus = _newUserAddStatus as LiveData<UserDetails>

    private val _getUserInfoStatus = MutableLiveData<UserDetails>()
    val getUserInfoStatus = _getUserInfoStatus as LiveData<UserDetails>

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

}