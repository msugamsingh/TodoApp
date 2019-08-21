package com.msugamsingh.todoapp.util

import android.content.Context
import android.preference.PreferenceManager
import com.msugamsingh.todoapp.TimerActivity

class PrefUtil {

    companion object {
        private const val TIMER_LENGTH = "com.msugamsingh.todoapp.timer_length"

        fun getTimerLength(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(TIMER_LENGTH, 0L)
        }

        fun setTimerLength(context: Context, seconds: Long) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(TIMER_LENGTH, seconds)
            editor.apply()
        }

        private const val PREVIOUS_TIMER_LENGTH_ID = "com.msugamsingh.todoapp.previous_timer_length"

        fun getPreviousTimerLength(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_ID, 0L)
        }

        fun setPreviousTimerLength(context: Context, seconds: Long) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_ID, seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID = "com.msugamsingh.todoapp.timer_state"

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

        private const val TIME_REMAINING_ID = "com.msugamsingh.todoapp.time_remaining"

        fun getTimeRemaining(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(TIME_REMAINING_ID, 0L)
        }

        fun setTimeRemaining(context: Context, seconds: Long) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(TIME_REMAINING_ID, seconds)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID = "com.msugamsingh.todoapp.background_time"

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
            return listOf(pref.getString("PREF_TITLE", "__"), pref.getInt("PREF_EXP", 0), pref.getString("PREF_DESC", "__"))
        }

        private const val TIMER_NAME = "com.msugamsingh.todoapp.timer_name"

        fun setTimerName(context: Context, name: String) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(TIMER_NAME, name)
            editor.apply()
        }

        fun getTimerName(context: Context): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(TIMER_NAME, "")
        }

//        private const val TIMER_STOP  = "com.msugamsingh.todoapp.timer_stop_at"
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

        private const val RECORDING_TIMER = "com.msugamsingh.todoapp.recording_timer"

        fun setRecordingTime(context: Context, time: Long) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(RECORDING_TIMER, time)
            editor.apply()
        }

        fun getRecordedTime(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(RECORDING_TIMER, 0)
        }

        private const val MULTIPLE_TASKS = "com.msugamsingh.todoapp.multiple_tasks"

        fun setIfMultipleOptionIsOn(context: Context, value: Boolean) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putBoolean(MULTIPLE_TASKS, value)
            editor.apply()
        }

        fun getIfMultipleOptionIsOn(context: Context): Boolean {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getBoolean(MULTIPLE_TASKS, false)
        }

//        private const val DAYS_OF_WEEK = "com.msugamsingh.todoapp.days_of_week"
//
//        fun setDayOfTheWeek(context: Context, dayInt: Int) {
//            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
//            editor.putInt(DAYS_OF_WEEK, dayInt)
//            editor.apply()
//        }
//
//        fun getDayOfTheWeek(context: Context): Int {
//            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//            return preferences.getInt(DAYS_OF_WEEK, 1)
//        }

        // Functions for weekdays
        private const val GRAPH = "com.msugamsingh.todoapp.graph"

        private const val WEEKDAY_SUN = "com.msugamsingh.todoapp.sun"
        private const val WEEKDAY_MON = "com.msugamsingh.todoapp.mon"
        private const val WEEKDAY_TUE = "com.msugamsingh.todoapp.tue"
        private const val WEEKDAY_WED = "com.msugamsingh.todoapp.wed"
        private const val WEEKDAY_THU = "com.msugamsingh.todoapp.thu"
        private const val WEEKDAY_FRI = "com.msugamsingh.todoapp.fri"
        private const val WEEKDAY_SAT = "com.msugamsingh.todoapp.sat"


        fun setWorkTimeForGraph(context: Context, workTime: Float) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putFloat(GRAPH, workTime)
            editor.apply()
        }

        fun getWorkTimeForGraph(context: Context): Float {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getFloat(GRAPH, 0f)
        }


        fun setWorkTimeForSun(context: Context, workTime: Float) = editorForWeekDays(context, workTime, WEEKDAY_SUN)

        fun getWorkTimeForSun(context: Context): Float = getterForWeekDays(context, WEEKDAY_SUN)

        fun setWorkTimeForMon(context: Context, workTime: Float) = editorForWeekDays(context, workTime, WEEKDAY_MON)

        fun getWorkTimeForMon(context: Context): Float = getterForWeekDays(context, WEEKDAY_MON)

        fun setWorkTimeForTue(context: Context, workTime: Float) = editorForWeekDays(context, workTime, WEEKDAY_TUE)

        fun getWorkTimeForTue(context: Context): Float = getterForWeekDays(context, WEEKDAY_TUE)

        fun setWorkTimeForWed(context: Context, workTime: Float) = editorForWeekDays(context, workTime, WEEKDAY_WED)

        fun getWorkTimeForWed(context: Context): Float = getterForWeekDays(context, WEEKDAY_WED)

        fun setWorkTimeForThu(context: Context, workTime: Float) = editorForWeekDays(context, workTime, WEEKDAY_THU)

        fun getWorkTimeForThu(context: Context): Float = getterForWeekDays(context, WEEKDAY_THU)

        fun setWorkTimeForFri(context: Context, workTime: Float) = editorForWeekDays(context, workTime, WEEKDAY_FRI)

        fun getWorkTimeForFri(context: Context): Float = getterForWeekDays(context, WEEKDAY_FRI)

        fun setWorkTimeForSat(context: Context, workTime: Float) = editorForWeekDays(context, workTime, WEEKDAY_SAT)

        fun getWorkTimeForSat(context: Context): Float = getterForWeekDays(context, WEEKDAY_SAT)


        private fun editorForWeekDays(context: Context, workTime: Float, const: String) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putFloat(const, workTime)
            editor.apply()
        }

        private fun getterForWeekDays(context: Context, const: String): Float {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getFloat(const, 0f)
        }

        private const val USER_NAME = "com.msugamsingh.todoapp.user_name"

        fun setUserName(context: Context, name: String) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(USER_NAME, name)
            editor.apply()
        }

        fun getUserName(context: Context): String {
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getString(USER_NAME, "")
        }

        private const val BATTERY_SAVER = "com.msugamsingh.todoapp.battery_saver"

        fun setCanUseBatterySaver(context: Context, boolean: Boolean) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                putBoolean(BATTERY_SAVER, boolean)
            }.apply()
        }

        fun getCanUseBatterySaver(context: Context): Boolean {
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getBoolean(BATTERY_SAVER, true)
        }

        private const val QUOTE_OF_THE_DAY = "com.msugamsingh.todoapp.quote_of_the_day"

        fun setIfQuoteShown(context: Context, boolean: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                putBoolean(QUOTE_OF_THE_DAY, boolean)
            }.apply()
        }

        fun getIfQuoteShown(context: Context): Boolean {
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getBoolean(QUOTE_OF_THE_DAY, false)
        }

        private const val UI_MODE = "com.msugamsingh.todoapp.ui_mode"

        fun setUIMode(context: Context, value: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                putBoolean(UI_MODE, value)
            }.apply()
        }

        fun getUIMode(context: Context): Boolean {
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getBoolean(UI_MODE, false)
        }


        private const val SHOW_QUOTE = "com.msugamsingh.todoapp.show_quote"

        fun setIfToShowQuote(context: Context, value: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                putBoolean(SHOW_QUOTE, value)
            }.apply()
        }

        fun getIfToShowQuote(context: Context): Boolean {
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getBoolean(SHOW_QUOTE, true)
        }

        private const val DAY_OF_YEAR = "com.msugamsingh.todoapp.day_of_year"

        fun setDate(context: Context, dayInt: Int) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                putInt(DAY_OF_YEAR, dayInt)
            }.apply()
        }

        fun getDate(context: Context): Int {
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getInt(DAY_OF_YEAR, 0)
        }
    }


}