package com.bl.chatapp.networking.retrofit

import com.bl.chatapp.common.Constants.FIREBASE_MESSAGING_SENDER_API_KEY
import com.bl.chatapp.networking.retrofit.PushNotificationApi.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class PushNotificationSenderService {
    private val api = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()
        .create<PushNotificationApi>()

    suspend fun sendNotification(message: PushMessage) : PushResponse {
        return api.sendPushNotification(FIREBASE_MESSAGING_SENDER_API_KEY, message)
    }
}