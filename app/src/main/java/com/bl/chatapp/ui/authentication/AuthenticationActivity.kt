package com.bl.chatapp.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants
import com.bl.chatapp.common.Constants.SPLASH_SCREEN_VISIBILITY
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.databinding.ActivityAuthenticationBinding
import com.bl.chatapp.ui.home.HomeActivity
import com.bl.chatapp.wrappers.UserDetails

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SharedPref.initializePref(this)
        val splashStatus = intent.getBooleanExtra(SPLASH_SCREEN_VISIBILITY, true)
        if(splashStatus) {
            gotoSplashScreen()
        } else {
            gotoLoginPage()
        }
    }

    private fun gotoSplashScreen() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_id, SplashFragment()).commit()
    }

    fun gotoLoginPage() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_id, LoginScreenFragment()).commit()
    }

    fun gotoHomeActivity(userId: String) {
        val userDetails = UserDetails(userId, false)
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(Constants.USER_DETAILS, userDetails)
        finish()
        startActivity(intent)
    }
}