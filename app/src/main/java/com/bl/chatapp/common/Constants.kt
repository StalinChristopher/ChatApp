package com.bl.chatapp.common

object Constants {
    const val VERIFICATION_ID_TOKEN = "verification_id_token"
    const val PHONE_NUMBER = "phone_number"
    const val USER_DETAILS = "user_details"
    const val DEFAULT_USER_ID = "default_user_id"
    const val SPLASH_SCREEN_VISIBILITY = "SPLASH_SCREEN_STATUS"
    const val IMAGE_FROM_GALLERY_CODE = 100
    const val STORAGE_PERMISSION_CODE = 111
    const val OTP = "OTP"
    const val TEXT = "text"
    const val IMAGE = "image"
    const val IMAGE_PATH = "IMAGE_PATH"
    const val CONVERSATION_TYPE = "CONVERSATION_TYPE"
    const val CHAT = "CHAT"
    const val GROUP = "GROUP"

    //firebase key names (key value pairs)
    const val FIREBASE_USERNAME = "userName"
    const val FIREBASE_STATUS = "status"
    const val FIREBASE_PHONE = "phoneNumber"
    const val FIREBASE_PROFILE_IMAGE_URL = "profileImageUrl"
    const val FIREBASE_TOKEN = "firebaseToken"
    const val VIEW_TYPE_SENT = 1
    const val VIEW_TYPE_RECEIVE = 2
    const val VIEW_TYPE_IMAGE_SENT = 3
    const val VIEW_TYPE_IMAGE_RECEIVE = 4

    // fireStore collections
    const val FIREBASE_USERS_COLLECTION = "users"
    const val FIREBASE_CHATS_COLLECTION = "Chats"
    const val FIREBASE_MESSAGES_COLLECTION = "Messages"
    const val FIREBASE_GROUP_CHATS_COLLECTION = "groupChat"

    //firebase storage
    const val FIREBASE_PROFILE_IMAGES_ = "profileImages"
    const val FIREBASE_CHAT_IMAGES = "chat_images"
    const val FIREBASE_GROUP_CHAT_IMAGES = "group_chat_images"

    //chat details fragment
    const val CURRENT_USER = "CURRENT_USER"
    const val FOREIGN_USER = "FOREIGN_USER"

    //MESSAGE KEYS
    const val MESSAGE_ID = "messageId"
    const val SENDER_ID = "senderId"
    const val RECEIVER_ID = "receiverId"
    const val SENT_TIME = "sentTime"
    const val CONTENT = "content"
    const val MESSAGE_TYPE = "messageType"
    const val PARTICIPANTS = "participants"

    const val MESSAGE_LIST = "MESSAGE_LIST"
    const val SELECTED_GROUP = "SELECTED_GROUP"

    //view image activity
    const val VIEW_IMAGE_ACTIVITY_REQUEST_CODE = 113
    const val VIEW_IMAGE_ACTIVITY_RESULT_CODE = 0

    //firebase messaging token sharedPref key
    const val FIREBASE_MESSAGING_TOKEN_SHAREDPREF = "FirebaseMessagingToken"

    const val FIREBASE_MESSAGING_SENDER_API_KEY = "key=AAAAApSY3m4:APA91bHoxBLm5EL71AJUFSD8PmCCqMVvhy259clIiIjqUUmVVXng_h2Cr0mVUgPOOJe_y6V4PkZLLLXK2EP4LvDfAtQLckb7kcVc6_hQZGI-PEKoq47aksSqbTWlIyxvuNe7CPtpSzmp"





}