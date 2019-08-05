package com.msugamsingh.todoapp.util

import android.content.Context
import android.preference.PreferenceManager
import com.msugamsingh.todoapp.TimerActivity

class PrefUtil {

    companion object {
        private const val TIMER_LENGTH = "com.msugamsingh.timerapp.timer_length"

        fun getTimerLength(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(TIMER_LENGTH, 0L)
        }

        fun setTimerLength(context: Context, seconds: Long) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(TIMER_LENGTH, seconds)
            editor.apply()
        }

        private const val PREVIOUS_TIMER_LENGTH_ID = "com.msugamsingh.timerapp.previous_timer_length"

        fun getPreviousTimerLength(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_ID, 0L)
        }

        fun setPreviousTimerLength(context: Context, seconds: Long) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_ID, seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID = "com.msugamsingh.timerapp.timer_state"

        fun getTimerState(context: Context): TimerActivity.TimerState {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0) // The Default value of TimerState[0] is Stopped
            return TimerActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(context: Context, state: TimerActivity.TimerState) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal     // will return an int value (or rather index of the item)
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private const val TIME_REMAINING_ID = "com.msugamsingh.timerapp.time_remaining"

        fun getTimeRemaining(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(TIME_REMAINING_ID, 0L)
        }

        fun setTimeRemaining(context: Context, seconds: Long) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(TIME_REMAINING_ID, seconds)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID = "com.msugamsingh.timerapp.background_time"

        fun getAlarmTime(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmTime(context: Context, time: Long) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }

        fun setTaskPref(context: Context, title: String, description: String, expectedTime: Int) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            with(editor) {
                putString("PREF_TITLE", title)
                putString("PREF_DESC", description)
                putInt("PREF_EXP", expectedTime)
            }
            editor.apply()
        }

        fun getTaskPref(context: Context): List<Any> {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return listOf(pref.getString("PREF_TITLE", ""), pref.getInt("PREF_EXP", 0), pref.getString("PREF_DESC", ""))
        }

        private const val TIMER_NAME = "com.msugamsingh.timerapp.timer_name"

        fun setTimerName(context: Context, name: String) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(TIMER_NAME, name)
            editor.apply()
        }

        fun getTimerName(context: Context): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(TIMER_NAME, "")
        }

//        private const val TIMER_STOP  = "com.msugamsingh.timerapp.timer_stop_at"
//
//        fun setTimerStopTime(context: Context, stoppedIn: Long) {
//            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
//            editor.putLong(TIMER_STOP, stoppedIn)
//            editor.apply()
//        }
//
//        fun getTimerStopTime(context: Context): Long {
//            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//            return preferences.getLong(TIMER_STOP, 0)
//        }

        private const val RECORDING_TIMER  = "com.msugamsingh.timerapp.recording_timer"

        fun setRecordingTime(context: Context, time: Long) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(RECORDING_TIMER, time)
            editor.apply()
        }

        fun getRecordedTime(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(RECORDING_TIMER, 0)
        }

        private const val MULTIPLE_TASKS = "com.msugamsingh.timerapp.multiple_tasks"

        fun setIfMultipleOptionIsOn(context: Context, value: Boolean) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putBoolean(MULTIPLE_TASKS, value)
            editor.apply()
        }

        fun getIfMultipleOptionIsOn(context: Context) : Boolean {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getBoolean(MULTIPLE_TASKS, false)
        }
    }
}