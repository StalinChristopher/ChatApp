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
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _getUserListStatus = MutableLiveData<ArrayList<UserDetails>>()
    val getUserListStatus = _getUserListStatus as LiveData<ArrayList<UserDetails>>

    private val _getAllChatsOfUserStatus = MutableLiveData<ArrayList<Chat>>()
    val getAllChatsOfUserStatus = _getAllChatsOfUserStatus as LiveData<ArrayList<Chat>>

    fun getUserListFromDb(context: Context, user: UserDetails) {
        viewModelScope.launch {
            val userList = DatabaseLayer.getInstance(context).getUserListFromDb(user)
            if (userList != null) {
                _getUserListStatus.postValue(userList)
            }
        }
    }

    fun getAllChatsOfUser(context: Context) {
        viewModelScope.launch {
            val uid = SharedPref.getInstance(context).getUserId()
            val chatList = DatabaseLayer.getInstance(context).getAllChatsOfUser(uid)
            if (chatList != null) {
                _getAllChatsOfUserStatus.postValue(chatList)
            }
        }
    }

}