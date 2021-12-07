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

    //firebase key names (key value pairs)
    const val FIREBASE_USERNAME = "userName"
    const val FIREBASE_STATUS = "status"
    const val FIREBASE_PHONE = "phoneNumber"
    const val FIREBASE_PROFILE_IMAGE_URL = "profileImageUrl"
    const val VIEW_TYPE_SENT = 1
    const val VIEW_TYPE_RECEIVE = 2

    // fireStore collections
    const val FIREBASE_USERS_COLLECTION = "users"
    const val FIREBASE_CHATS_COLLECTION = "Chats"
    const val FIREBASE_MESSAGES_COLLECTION = "Messages"

    //firebase storage
    const val FIREBASE_PROFILE_IMAGES_ = "profileImages"

    //chat details fragment
    const val CURRENT_USER = "CURRENT_USER"
    const val FOREIGN_USER = "FOREIGN_USER"

    //MESSAGE KEYS
    const val MESSAGE_ID = "messageId"
    const val SENDER_ID = "senderId"
    const val RECEIVER_ID = "receiverId"
    const val SENT_TIME = "sentTime"
    const val MESSAGE_TEXT = "messageText"
    const val MESSAGE_TYPE = "messageType"
    const val PARTICIPANTS = "participants"

    const val MESSAGE_LIST = "MESSAGE_LIST"


}