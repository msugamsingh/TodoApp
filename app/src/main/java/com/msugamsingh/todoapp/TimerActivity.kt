package com.msugamsingh.todoapp

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityOptions
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.msugamsingh.todoapp.FragmentTimer.Companion.nowSeconds
import com.msugamsingh.todoapp.FragmentTimer.Companion.removeAlarm
import com.msugamsingh.todoapp.FragmentTimer.Companion.setAlarm
import com.msugamsingh.todoapp.FragmentTimer.Companion.slideTransition
import com.msugamsingh.todoapp.util.NotificationUtil
import com.msugamsingh.todoapp.util.PrefUtil
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*
import java.util.*

class TimerActivity : AppCompatActivity() {



    enum class TimerState {
        Stopped, Running, Paused
    }

    private lateinit var timer: CountDownTimer
    private var timerLength = 0L
    private var timerState = TimerState.Stopped
    private var timeRemaining = 0L
    lateinit var task: Task
    private lateinit var taskTitle: String
    private lateinit var taskDescription: String
    private var taskExpectedTime: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        if (PrefUtil.getTimerState(this) == TimerState.Running || PrefUtil.getTimerState(this) == TimerState.Paused) {
            val task = PrefUtil.getTaskPref(this)
            taskTitle = task[0].toString()
            taskDescription = task[2].toString()
            taskExpectedTime = task[1].toString().toInt()
        } else {
            task = intent.getParcelableExtra(TASK_OBJECT)
            taskTitle = task.title
            taskDescription = task.description
            taskExpectedTime = task.expectedTime
            PrefUtil.setTaskPref(this, taskTitle, taskDescription, taskExpectedTime)

        }

//        Log.d("TimerActivity", "is completed ${task.isCompleted}")

        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.white_drawable))
        supportActionBar?.title = taskTitle.toUpperCase()
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        Log.d("TimerActivity", "$taskTitle $taskDescription $taskExpectedTime")

        PrefUtil.setTimerLength(this, (taskExpectedTime).toLong())

        fab_play.setOnClickListener {
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        }

        fab_pause.setOnClickListener {
            timer.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }

        fab_stop.setOnClickListener {
            if (timerState == TimerState.Paused) onTimerFinished()
            else {
                timer.cancel()
                onTimerFinished()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        initTimer()
        removeAlarm(this)
        NotificationUtil.hideTimerNotification(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (PrefUtil.getTimerState(this) == TimerState.Stopped) onBackPressed()
        else startActivity(Intent(this, MainActivity::class.java))
        return true
    }

    override fun onPause() {
        super.onPause()

        if (timerState == TimerState.Running) {
            timer.cancel()
            val wakeUpTime = setAlarm(this, nowSeconds, timeRemaining)
//            PrefUtil.setLeftTime(this, timeRemaining.toInt())
            NotificationUtil.showTimerRunning(this, wakeUpTime)
        } else if (timerState == TimerState.Paused) {
            NotificationUtil.showTimerPaused(this)
        }

        PrefUtil.setPreviousTimerLength(this, timerLength)
        PrefUtil.setTimeRemaining(this, timeRemaining)
        PrefUtil.setTimerState(this, timerState)
    }

    private fun initTimer() {
        timerState = PrefUtil.getTimerState(this)

        // Maybe setNewTimer should be here

        if (timerState == TimerState.Stopped) setNewTimer()
        else setPreviousTimer()

        timeRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused)
            PrefUtil.getTimeRemaining(this)
        else timerLength

        val alarmSetTime = PrefUtil.getAlarmTime(this)
        if (alarmSetTime > 0) timeRemaining -= nowSeconds - alarmSetTime

        if (timeRemaining <= 0) onTimerFinished()
        else if (timerState == TimerState.Running) startTimer()
        updateButtons()
        updateCountdownUI()
    }

    fun onTimerFinished() {
        timerState = TimerState.Stopped
        setNewTimer()
        progress_ring.max = timerLength.toInt()
        progress_ring.progress = timerLength.toInt()
        PrefUtil.setTimeRemaining(this, timerLength)
        timeRemaining = timerLength
        updateButtons()
        updateCountdownUI()
    }


    // TIMER
    fun startTimer() {
        timerState = TimerState.Running
        PrefUtil.setTimerName(this, taskTitle)

        timer = object : CountDownTimer(timeRemaining *1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished < 15 * 1000) MainActivity.makeSound(applicationContext, R.raw.tick_sound)
                timeRemaining = millisUntilFinished/1000
                updateCountdownUI()
            }
        }.start()
    }

    fun setNewTimer() {
        val lengthInMin = PrefUtil.getTimerLength(this)
        timerLength = (lengthInMin * 60L)
        progress_ring.max = timerLength.toInt()
    }

    fun setPreviousTimer() {
        timerLength = PrefUtil.getPreviousTimerLength(this)
        progress_ring.max = timerLength.toInt()
    }

    @SuppressLint("SetTextI18n")
    fun updateCountdownUI() {
        Log.d("TimerActivity", "updateUI called with $timeRemaining")
        val minsUntilFinished = timeRemaining / 60
        val secsInMinsUntilFinished = timeRemaining - minsUntilFinished * 60
        val secStr = secsInMinsUntilFinished.toString()
//        PrefUtil.setLeftTime(this, timeRemaining.toInt())
        timer_countdown.text  = "$minsUntilFinished:${ if (secStr.length == 2) secStr
        else "0$secStr"}"
        progress_ring.progress = (timeRemaining).toInt()        // i dit it in order to run progress ring backwards
    }

    fun updateButtons() {
        when (timerState) {
            TimerState.Running -> {
                fab_play.hide()
                fab_pause.show()
                fab_stop.show()
            }
            TimerState.Stopped -> {
                fab_play.show()
                fab_pause.hide()
                fab_stop.hide()
            }
            TimerState.Paused -> {
                fab_play.show()
                fab_stop.show()
                fab_pause.hide()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.transition.
        return when (item.itemId) {
            R.id.action_settings -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) slideTransition(this, this)
                else startActivity(Intent(this, SettingsActivity::class.java))  // this is with default transition for android < 21
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}
