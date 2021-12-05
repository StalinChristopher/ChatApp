package com.bl.chatapp.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants
import com.bl.chatapp.common.Constants.DEFAULT_USER_ID
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.ui.home.HomeActivity
import com.bl.chatapp.wrappers.UserDetails

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        SharedPref.initializePref(this)
        val userId = SharedPref.getUserId()
        if(userId == DEFAULT_USER_ID) {
            gotoLoginPage()
        } else {
            gotoHomeActivity(userId)
        }

    }

    private fun gotoLoginPage() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_id, LoginScreenFragment()).commit()
    }

    private fun gotoHomeActivity(userId: String) {
        val userDetails = UserDetails(userId, false)
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(Constants.USER_DETAILS, userDetails)
        finish()
        startActivity(intent)
    }
}