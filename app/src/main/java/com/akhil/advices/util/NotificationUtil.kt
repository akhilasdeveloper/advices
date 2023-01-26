package com.akhil.advices.util

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.akhil.advices.R
import com.akhil.advices.ui.MainActivity
import com.akhil.advices.util.Constants.INTENT_KEY_COLOR1
import com.akhil.advices.util.Constants.INTENT_KEY_MESSAGE
import com.akhil.advices.util.Constants.NOTIFICATION_ID

class NotificationUtil(var context: Context) {

    private var notificationManagerCompat: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    fun showNotification(message: String, color1: Int, color2: Int) {

        val intent: Intent = Intent(context, MainActivity::class.java)
        intent.putExtra(INTENT_KEY_MESSAGE, message)
        intent.putExtra(INTENT_KEY_COLOR1, color1.toString())
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder: NotificationCompat.Builder =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {

                NotificationCompat.Builder(
                    context,
                    Constants.CHANNEL_ID
                ).setSmallIcon(R.drawable.ic_advices_icon)
                    .setColor(color1)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(message)

            } else {
                val remoteViews = RemoteViews(context.packageName, R.layout.notification_layout)
                remoteViews.setTextViewText(R.id.notification_text, message)

                val bmp: Bitmap = Bitmap.createBitmap(1024, 500, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bmp)
                canvas.drawColor(color1)

                remoteViews.setTextColor(R.id.notification_text, color2)
                remoteViews.setImageViewBitmap(R.id.notifBack, bmp)

                NotificationCompat.Builder(
                    context,
                    Constants.CHANNEL_ID
                ).setSmallIcon(R.drawable.ic_advices_icon)
                    .setCustomContentView(remoteViews)
                    .setColor(color1)
                    .setContentIntent(pendingIntent)
                    .setCustomHeadsUpContentView(remoteViews)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            }

        val notification: Notification = notificationBuilder.build()
        notificationManagerCompat.notify(NOTIFICATION_ID, notification)

    }


}