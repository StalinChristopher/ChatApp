package com.bl.chatapp.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bl.chatapp.R
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.ui.authentication.AuthenticationActivity
import com.bumptech.glide.Glide
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
        val notification = if(message.isBlank()) {
            generateImageNotification(title, image.toString())
        } else {
            generateMessageNotification(title, message)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
        notificationManager.notify(Random.nextInt(), notification)

    }

    private fun generateMessageNotification(title: String, message: String): Notification {
        val intent = Intent(this, AuthenticationActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_whatsapp_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setContentText(message)
            .setContentTitle(title)
            .build()
    }

    private fun generateImageNotification(title: String, image: String): Notification {
        val intent = Intent(this, AuthenticationActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val glideBitMap = Glide.with(this).asBitmap().load(image).submit()
        val bitMapImage = glideBitMap.get()
        val notificationBigPicture = NotificationCompat.BigPictureStyle().bigPicture(bitMapImage)

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_whatsapp_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setStyle(notificationBigPicture)
            .build()
    }
}
