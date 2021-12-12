package com.bl.chatapp.ui.home.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.chatapp.data.models.GroupInfo
import com.bl.chatapp.data.datalayer.DatabaseLayer
import com.bl.chatapp.wrappers.UserDetails
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GroupViewModel() : ViewModel() {
    val participants = ArrayList<GroupInfo>()

    private val _getAllGroupsStatus = MutableLiveData<Boolean>()
    val getAllGroupsStatus = _getAllGroupsStatus as LiveData<Boolean>

    fun getAllGroupsOfUser(currentUser: UserDetails) {
        viewModelScope.launch {
            DatabaseLayer().getAllGroupsOfUser(currentUser).collect {
                participants.clear()
                participants.addAll(it as ArrayList<GroupInfo>)
                _getAllGroupsStatus.postValue(true)
            }
        }
    }

}