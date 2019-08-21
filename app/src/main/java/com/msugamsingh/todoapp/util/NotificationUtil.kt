package com.msugamsingh.todoapp.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.msugamsingh.todoapp.*
import com.msugamsingh.todoapp.AppConstants.Companion.ACTION_PAUSE
import com.msugamsingh.todoapp.AppConstants.Companion.ACTION_RESUME
import com.msugamsingh.todoapp.AppConstants.Companion.ACTION_START
import com.msugamsingh.todoapp.AppConstants.Companion.ACTION_STOP
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtil {
    companion object {
        private const val CHANNEL_ID_TIMER = "menu_timer"
        private const val CHANNEL_NAME_TIMER = "Timer App Timer"
        private const val TIMER_ID = 0
//        var leftTime = 0
//        var showLeftTime = 0


        fun showTimerExpired(context: Context) {
            val startIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            startIntent.action = ACTION_START               // setting action so that we can access it later by using intent.action in other receiver or activity.
            val startPendingIntent = PendingIntent.getBroadcast(context, 0, startIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)      // Flag Update Current will only update the current pending intent not others

            // NOTIFICATION
            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
            nBuilder.setContentTitle("Time's Over")
                .setContentText("Start Again?")
                .setContentIntent( getPendingIntentWithStack(context, MainActivity(FragmentTimer())::class.java) )       // when timer stops, clicking on notification will direct to mainActivity
                .addAction(R.drawable.ic_play, "Start", startPendingIntent)

            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
            nManager.notify(TIMER_ID, nBuilder.build())

        }

        fun showTimerPaused(context: Context) {
            val resumeIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            resumeIntent.action = ACTION_RESUME
            val resumePendingIntent = PendingIntent.getBroadcast(context, 0, resumeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)      // Flag Update Current will only update the current pending intent not others

            // NOTIFICATION
            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
            nBuilder.setContentTitle("Timer's Paused.")
                .setContentText("Resume")
                .setContentIntent( getPendingIntentWithStack(context, TimerActivity::class.java) )
                .setOngoing(true)
                .addAction(R.drawable.ic_play, "Resume", resumePendingIntent)

            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
            nManager.notify(TIMER_ID, nBuilder.build())

        }

            fun showTimerRunning(context: Context, wakeUpTime: Long) {
                val stopIntent = Intent(context, TimerNotificationActionReceiver::class.java)
                stopIntent.action = ACTION_STOP
                val stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)

                val pauseIntent = Intent(context, TimerNotificationActionReceiver::class.java)
                pauseIntent.action = ACTION_PAUSE
                val pausePendingIntent = PendingIntent.getBroadcast(context, 0, pauseIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)


                // date format
                val df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

                // NOTIFICATION
                val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
                nBuilder.setContentTitle("Timer's Running.")
                    .setContentIntent( getPendingIntentWithStack(context, TimerActivity::class.java) )
                    .setContentText("Timer will stop at: ${df.format(Date(wakeUpTime))}")
                    .setOngoing(true)
                    .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
                    .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
                val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
                nManager.notify(TIMER_ID, nBuilder.build())

        }

        fun hideTimerNotification(context: Context) {
            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.cancel(TIMER_ID)
        }

        private fun getBasicNotificationBuilder(context: Context, channelId: String, playSound: Boolean): NotificationCompat.Builder {
            val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val nBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_timer_black_24dp)
                .setAutoCancel(true)
                .setDefaults(0)
            if (playSound) nBuilder.setSound(notificationSound)
            return nBuilder
        }

        private fun <T> getPendingIntentWithStack(context: Context, javaClass: Class<T>): PendingIntent {
            val resultIntent = Intent(context, javaClass)
            resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(javaClass)
            stackBuilder.addNextIntent(resultIntent)

            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        // extension fun
        private fun NotificationManager.createNotificationChannel(channelId: String, channelName: String, playSound: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
                                            else NotificationManager.IMPORTANCE_LOW
                val nChannel = NotificationChannel(channelId, channelName, channelImportance)
                nChannel.enableLights(false)
                this.createNotificationChannel(nChannel)

            }
        }
    }
}