package com.example.dompetku.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.dompetku.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        val channel = NotificationChannel(
            "HEADS_UP_NOTIFICATION",
            "Dompetku",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        val notification = android.app.Notification.Builder(this, "HEADS_UP_NOTIFICATION")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.emoney)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}