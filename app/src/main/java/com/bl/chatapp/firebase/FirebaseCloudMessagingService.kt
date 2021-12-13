package com.bl.chatapp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bl.chatapp.R
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.ui.authentication.AuthenticationActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseCloudMessagingService : FirebaseMessagingService() {

    private val firebaseMessaging = Firebase.messaging
    companion object {
        const val TAG = "FirebaseMessagingService"
        const val CHANNEL_ID = "ChatApp_channel_id"
        const val CHANNEL_NAME = "ChatApp channel"
    }

    fun generateFirebaseMessagingToken(callback: (String) -> Unit){
        firebaseMessaging.token.addOnCompleteListener {
            if(!it.isSuccessful) {
                Log.e(TAG, "Firebase FCM token fetch failed")
                callback("")
            }
            val tokenString = it.result.toString()
            callback(tokenString)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val message = remoteMessage.notification?.body ?: ""
        val title = remoteMessage.notification?.title ?: ""
        val image = remoteMessage.notification?.imageUrl ?: ""

        val userId = SharedPref.getInstance(this).getUserId()
        val intent = Intent(this, AuthenticationActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.whatsapp_logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setContentText(message)
            .setContentTitle(title)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
        notificationManager.notify(Random.nextInt(), notification)

    }
}
