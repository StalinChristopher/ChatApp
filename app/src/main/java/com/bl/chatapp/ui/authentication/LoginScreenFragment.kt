package com.bl.chatapp.ui.authentication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.PHONE_NUMBER
import com.bl.chatapp.databinding.LoginFragmentBinding
import com.bl.chatapp.viewmodels.LoginViewModel
import com.bl.chatapp.viewmodels.ViewModelFactory

class LoginScreenFragment : Fragment(R.layout.login_fragment) {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: LoginFragmentBinding
    private var mobileNumber: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        loginViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(LoginViewModel())
        )[LoginViewModel::class.java]
        login()
        observers()
    }

    private fun login() {
        var countryCodePicker = binding.countryCode
        binding.sendButton.setOnClickListener {
            val phoneNumber = binding.phoneLoginEditText.text.trim().toString()
            when {
                phoneNumber.length != 10 -> {
                    binding.phoneLoginEditText.error = getString(R.string.invalid_phone_error)
                }
                phoneNumber.isBlank() -> {
                    binding.phoneLoginEditText.error = getString(R.string.blank_phone_number_error)
                }
                else -> {
                    var code = countryCodePicker.selectedCountryCodeWithPlus
                    mobileNumber = "$code$phoneNumber"
                    Log.i("login", mobileNumber)
                    loginViewModel.loginWithPhoneNumber(requireActivity(), mobileNumber, true)

                }
            }
        }
    }

    private fun observers() {
        loginViewModel.gotoOtpVerificationPageStatus.observe(viewLifecycleOwner) {
            if (it) {
                gotoOtpFragment()
            }
        }

        loginViewModel.invalidPhoneNumberStatus.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun gotoOtpFragment() {
        activity?.run {
            var bundle = Bundle()
            bundle.putString(PHONE_NUMBER, mobileNumber)
            var otpFragment = OtpVerificationFragment()
            otpFragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_id, otpFragment).commit()
        }
    }
}