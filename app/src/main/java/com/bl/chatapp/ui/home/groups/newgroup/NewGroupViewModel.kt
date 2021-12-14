package com.bl.chatapp.ui.home.groups.newgroup

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.chatapp.data.datalayer.DatabaseLayer
import com.bl.chatapp.wrappers.UserDetails
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewGroupViewModel: ViewModel() {
    val userList = ArrayList<UserDetails>()
    private val databaseLayer = DatabaseLayer()

    private val _getUserListStatus = MutableLiveData<Boolean>()
    val getUserListStatus = _getUserListStatus as LiveData<Boolean>

    private val _groupCreatedStatus = MutableLiveData<Boolean>()
    val groupCreatedStatus = _groupCreatedStatus as LiveData<Boolean>

    private val _setGroupImageStatus = MutableLiveData<String>()
    val setGroupImageStatus = _setGroupImageStatus as LiveData<String>

    fun getUserListFromDb(userDetails: UserDetails) {
        viewModelScope.launch {
            databaseLayer.getUserListFromDb(userDetails).collect {
                userList.clear()
                userList.addAll(it as ArrayList<UserDetails>)
                _getUserListStatus.postValue(true)
            }
        }
    }

    fun createGroup(participants: ArrayList<String>, groupName: String, groupImageUrl: String) {
        viewModelScope.launch {
            val status = DatabaseLayer().createGroup(participants, groupName, groupImageUrl)
            if(status) {
                _groupCreatedStatus.postValue(true)
            }
        }
    }

    fun setGroupImage(imageUri: Uri) {
        viewModelScope.launch {
            val url = databaseLayer.setGroupImageInCloud(imageUri)
            if(url != null) {
                _setGroupImageStatus.postValue(url)
            }
        }
    }
}