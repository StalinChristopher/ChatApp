package com.bl.chatapp.ui.authentication

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.databinding.SplashFragmentBinding

class SplashFragment : Fragment(R.layout.splash_fragment) {
    private lateinit var binding: SplashFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SplashFragmentBinding.bind(view)
        binding.splashScreenImage.alpha = 0f
        binding.splashScreenImage.animate().setDuration(1500).alpha(1f).withEndAction {
            val userId = SharedPref.getInstance(requireContext()).getUserId()
            if (userId == Constants.DEFAULT_USER_ID) {
                (activity as AuthenticationActivity).gotoLoginPage()
            } else {
                (activity as AuthenticationActivity).gotoHomeActivity(userId)
            }
        }
    }
}