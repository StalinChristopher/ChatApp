package com.bl.chatapp.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.data.models.Chat
import com.bl.chatapp.data.services.DatabaseLayer
import com.bl.chatapp.wrappers.UserDetails
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(private val context: Context) : ViewModel() {

    val userList = ArrayList<UserDetails>()

    private val _getUserListStatus = MutableLiveData<Boolean>()
    val getUserListStatus = _getUserListStatus as LiveData<Boolean>

    fun getUserListFromDb(context: Context, userDetails: UserDetails) {
        viewModelScope.launch {
            DatabaseLayer.getInstance(context).getUserListFromDb(userDetails).collect {
                userList.clear()
                userList.addAll(it as ArrayList<UserDetails>)
                _getUserListStatus.postValue(true)
            }
        }
    }
}