package com.bl.chatapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.chatapp.data.services.DatabaseLayer
import com.bl.chatapp.wrappers.UserDetails
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GroupViewModel : ViewModel() {
    val participants = ArrayList<String>()

    fun getAllGroupsOfUser(currentUser: UserDetails) {
        viewModelScope.launch {
            DatabaseLayer().getAllGroupsOfUser(currentUser).collect {
                Log.i("GroupViewModel","$it")
            }
        }
    }

    fun createGroup() {
//        participants.add("OdjZz8q5FUWt5qLYjdouQQPUmVD3") //TestUser
//        participants.add("OxP2T8HSGmaGtxfLRwE6CvjDinN2") //Stalin
//        participants.add("DGc9fg0qsygUWtwZ3UygFoADcpP2") //Test2
        participants.clear()
        participants.add("OdjZz8q5FUWt5qLYjdouQQPUmVD3") //TestUser
        participants.add("8xxko917RPWVnlxOsASc4LiZDvJ2") //Jio user
        participants.add("DGc9fg0qsygUWtwZ3UygFoADcpP2")
        viewModelScope.launch {
            DatabaseLayer().createGroup(participants, "Office")
        }
    }
}