package com.bl.chatapp.ui.view_mage

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.CHAT
import com.bl.chatapp.common.Constants.CONVERSATION_TYPE
import com.bl.chatapp.common.Constants.IMAGE_PATH
import com.bl.chatapp.common.Constants.VIEW_IMAGE_ACTIVITY_RESULT_CODE
import com.bl.chatapp.databinding.ActivityViewImageBinding
import com.bl.chatapp.ui.home.chats.chatdetails.ChatDetailViewModel
import com.bl.chatapp.ui.home.chats.chatdetails.ChatDetailsActivity

class ViewImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewImageBinding
    private lateinit var imageUri: Uri
    private lateinit var imageString: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        binding = ActivityViewImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getFromIntent()
        listeners()
    }

    private fun listeners() {
        binding.viewImageBackButton.setOnClickListener {
            finish()
        }

        binding.viewImageFloatingButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra(IMAGE_PATH,imageString)
            setResult(VIEW_IMAGE_ACTIVITY_RESULT_CODE, intent)
            finish()
        }
    }

    private fun getFromIntent() {
        imageString = intent.getStringExtra(IMAGE_PATH).toString()
        imageUri = Uri.parse(imageString)
        binding.viewImageImageView.setImageURI(imageUri)

    }
}