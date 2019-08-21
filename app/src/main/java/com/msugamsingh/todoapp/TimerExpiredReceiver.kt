package com.msugamsingh.todoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.msugamsingh.todoapp.util.NotificationUtil
import com.msugamsingh.todoapp.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        NotificationUtil.showTimerExpired(context)
        PrefUtil.setTimerState(context, TimerActivity.TimerState.Stopped)
        PrefUtil.setAlarmTime(context, 0)
    }
}
