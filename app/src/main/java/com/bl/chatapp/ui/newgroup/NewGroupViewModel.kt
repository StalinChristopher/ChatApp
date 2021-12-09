package com.bl.chatapp.ui.newgroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.chatapp.data.services.DatabaseLayer
import com.bl.chatapp.wrappers.UserDetails
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewGroupViewModel: ViewModel() {
    val userList = ArrayList<UserDetails>()
    private val databaseLayer = DatabaseLayer()

    private val _getUserListStatus = MutableLiveData<Boolean>()
    val getUserListStatus = _getUserListStatus as LiveData<Boolean>

    fun getUserListFromDb(userDetails: UserDetails) {
        viewModelScope.launch {
            databaseLayer.getUserListFromDb(userDetails).collect {
                userList.clear()
                userList.addAll(it as ArrayList<UserDetails>)
                _getUserListStatus.postValue(true)
            }
        }
    }
}