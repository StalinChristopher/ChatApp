package com.bl.chatapp.data.services

import android.content.Context
import android.util.Log
import com.bl.chatapp.wrappers.UserDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseLayer(private val context: Context) {
    private var fireStoreDb: FirebaseDatabaseService = FirebaseDatabaseService()

    companion object {
        private val instance: DatabaseLayer? by lazy { null }
        fun getInstance(context: Context): DatabaseLayer = instance ?: DatabaseLayer(context)
    }

    suspend fun addUserInfoToDatabase(userDetails: UserDetails): UserDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val userFromFirebase = fireStoreDb.addUserInfoToDatabase(userDetails)
                userFromFirebase
            } catch (e: Exception) {
                Log.e("DatabaseService", "Database add failed")
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getUserInfoFromDatabase(userDetails: UserDetails): UserDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val userFromFirebase = fireStoreDb.getUserInfoFromDatabase(userDetails)
                userFromFirebase
            } catch (e: Exception) {
                Log.e("DatabaseService", "Database user fetch failed")
                null
            }
        }
    }
}