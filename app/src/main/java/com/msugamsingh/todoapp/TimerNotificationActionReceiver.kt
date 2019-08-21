package com.msugamsingh.todoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.msugamsingh.todoapp.AppConstants.Companion.ACTION_PAUSE
import com.msugamsingh.todoapp.AppConstants.Companion.ACTION_RESUME
import com.msugamsingh.todoapp.AppConstants.Companion.ACTION_START
import com.msugamsingh.todoapp.AppConstants.Companion.ACTION_STOP
import com.msugamsingh.todoapp.FragmentTimer.Companion.nowSeconds
import com.msugamsingh.todoapp.FragmentTimer.Companion.removeAlarm
import com.msugamsingh.todoapp.FragmentTimer.Companion.setAlarm
import com.msugamsingh.todoapp.util.NotificationUtil
import com.msugamsingh.todoapp.util.PrefUtil

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_STOP -> {
                removeAlarm(context)
                PrefUtil.setTimerState(context, TimerActivity.TimerState.Stopped)
                NotificationUtil.hideTimerNotification(context)
            }
            ACTION_PAUSE -> {
                var timeRemaining = PrefUtil.getTimeRemaining(context)
                val alarmSetTime = PrefUtil.getAlarmTime(context)
                val nowSeconds = nowSeconds

                timeRemaining -= nowSeconds - alarmSetTime
                PrefUtil.setTimeRemaining(context, timeRemaining)

                removeAlarm(context)
                PrefUtil.setTimerState(context, TimerActivity.TimerState.Paused)
                NotificationUtil.showTimerPaused(context)
            }
            ACTION_RESUME -> {
                var timeRemaining = PrefUtil.getTimeRemaining(context)
                val wakeUpTime = setAlarm(context, nowSeconds, timeRemaining)
                PrefUtil.setTimerState(context, TimerActivity.TimerState.Running)
                NotificationUtil.showTimerRunning(context, wakeUpTime)

            }
            ACTION_START -> {
                val minuteRemaining = PrefUtil.getTimerLength(context)
                val timeRemaining = minuteRemaining * 60L
                val wakeUpTime = setAlarm(context, nowSeconds, timeRemaining)
                PrefUtil.setTimerState(context, TimerActivity.TimerState.Running)
                PrefUtil.setTimeRemaining(context, timeRemaining)           // try removing it
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
        }
    }
}
