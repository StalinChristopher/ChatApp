package com.bl.chatapp.ui.authentication

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.FIREBASE_MESSAGING_TOKEN_SHAREDPREF
import com.bl.chatapp.common.Constants.OTP
import com.bl.chatapp.common.Constants.PHONE_NUMBER
import com.bl.chatapp.common.Constants.USER_DETAILS
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.databinding.OtpVerificationFragmentBinding
import com.bl.chatapp.ui.home.HomeActivity
import com.bl.chatapp.viewmodels.SharedViewModel
import com.bl.chatapp.viewmodels.ViewModelFactory
import com.bl.chatapp.wrappers.UserDetails

class OtpVerificationFragment : Fragment(R.layout.otp_verification_fragment) {
    private lateinit var binding: OtpVerificationFragmentBinding
    private var phoneNumber: String = ""
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var verifyButton: Button
    private lateinit var resendOtpButton: TextView
    private lateinit var otpEditText: EditText
    private lateinit var pleaseWaitDialog: Dialog
    private lateinit var sharedViewModel: SharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = OtpVerificationFragmentBinding.bind(view)
        loginViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(LoginViewModel())
        )[LoginViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        pleaseWaitDialog = Dialog(requireContext())
        pleaseWaitDialog.setContentView(R.layout.dialog_loading)
        phoneNumber = arguments?.get(PHONE_NUMBER) as String
        verifyButton = binding.verifiyButtonOtpScreen
        otpEditText = binding.otpEditText
        resendOtpButton = binding.resendTextView
        listeners()
        observers()
        savedInstanceState?.let {
            binding.otpEditText.setText(it.get(OTP) as String)
        }
    }

    private fun listeners() {

        verifyButton.setOnClickListener {
            val otp = otpEditText.text.toString().trim()
            if (otp.isEmpty()) {
                otpEditText.error = getString(R.string.enter_otp)
            } else {
                loginViewModel.verifyOtp(otp, phoneNumber!!)
                pleaseWaitDialog.show()
            }
        }

        resendOtpButton.setOnClickListener {
            loginViewModel.loginWithPhoneNumber(requireActivity(), phoneNumber, false)
        }

    }

    private fun observers() {
        loginViewModel.gotoHomePageStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                if(it.newUser) {
                    gotoNewUserFragment(it)
                } else {
                    val token = SharedPref.getInstance(requireContext()).getValue(
                        FIREBASE_MESSAGING_TOKEN_SHAREDPREF)
                    Log.i("OTP_VERIFICATION", token)
                    sharedViewModel.getUserData(it, token)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
            pleaseWaitDialog.dismiss()
        }

        sharedViewModel.getUserInfoStatus.observe(viewLifecycleOwner) {
            if(it != null) {
                SharedPref.getInstance(requireContext()).addUserId(it.uid)
                gotoHomeActivity(it)
            }
        }
    }

    private fun gotoNewUserFragment(userDetails: UserDetails) {
        activity?.run {
            val bundle = Bundle()
            bundle.putSerializable(USER_DETAILS,userDetails)
            var newUserFragment = NewUserInfoFragment()
            newUserFragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_id, newUserFragment).commit() }
    }

    private fun gotoHomeActivity(userDetails: UserDetails) {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.putExtra(USER_DETAILS, userDetails)
        requireActivity().finish()
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(OTP,otpEditText.text.toString())
    }
}