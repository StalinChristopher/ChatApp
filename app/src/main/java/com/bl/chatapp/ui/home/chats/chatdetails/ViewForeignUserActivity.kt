package com.bl.chatapp.ui.home.chats.chatdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.FOREIGN_USER
import com.bl.chatapp.databinding.ActivityViewForeignUserBinding
import com.bl.chatapp.wrappers.UserDetails

class ViewForeignUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewForeignUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewForeignUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var foreignUser = intent.getSerializableExtra(FOREIGN_USER) as UserDetails

        binding.foreignUserNameTextView.text = foreignUser.userName
        binding.foreignUserStatusTextView.text = foreignUser.status
        binding.foreignUserPhoneTextView.text = foreignUser.phone

        binding.foreignUserBackButton.setOnClickListener {
            finish()
        }

    }
}