package com.bl.chatapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bl.chatapp.common.Constants.SPLASH_SCREEN_VISIBILITY
import com.bl.chatapp.common.Constants.USER_DETAILS
import com.bl.chatapp.databinding.ActivityHomeBinding
import com.bl.chatapp.ui.authentication.AuthenticationActivity
import com.bl.chatapp.viewmodels.LoginViewModel
import com.bl.chatapp.viewmodels.ViewModelFactory
import com.bl.chatapp.wrappers.UserDetails

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var logOutButton: Button
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logOutButton = binding.logoutButton
        loginViewModel = ViewModelProvider(this, ViewModelFactory(this))[LoginViewModel::class.java]
        listeners()
        var user = intent.getSerializableExtra(USER_DETAILS) as UserDetails
        Log.i("HomeActivity","$user")
    }

    private fun listeners() {
        binding.logoutButton.setOnClickListener {
            Log.i("HomeActivity", "Reached")
            loginViewModel.logOut()
            gotoAuthenticationActivity()
        }
    }

    private fun gotoAuthenticationActivity() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.putExtra(SPLASH_SCREEN_VISIBILITY,false)
        startActivity(intent)
    }
}