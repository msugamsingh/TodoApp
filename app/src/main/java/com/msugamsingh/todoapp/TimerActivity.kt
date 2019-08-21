package com.msugamsingh.todoapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.msugamsingh.todoapp.FragmentTimer.Companion.nowSeconds
import com.msugamsingh.todoapp.FragmentTimer.Companion.removeAlarm
import com.msugamsingh.todoapp.FragmentTimer.Companion.setAlarm
import com.msugamsingh.todoapp.util.NotificationUtil
import com.msugamsingh.todoapp.util.PrefUtil
import kotlinx.android.synthetic.main.content_timer.*

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
    private var timerStopAt = 0L
    private var timerStopIn = 0L
    private var minsUntilFinished = 0L
    private var secsInMinsUntilFinished = 0L
    private var secStr = ""
    private var initialTime = 0L
    private var recordedTime = 0L
    private var timeForGraph = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_timer)

        val task = PrefUtil.getTaskPref(this)
        taskTitle = task[0].toString()
        taskDescription = task[2].toString()
        taskExpectedTime = task[1].toString().toInt()


        /* I dont see any relevent use of this
        else {
            task = intent.getParcelableExtra(TASK_OBJECT)
            taskTitle = task.title
            taskDescription = task.description
            taskExpectedTime = task.expectedTime
            PrefUtil.setTaskPref(this, taskTitle, taskDescription, taskExpectedTime)

        }
*/

        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.white_drawable))
        supportActionBar?.title = taskTitle.toUpperCase()
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        PrefUtil.setTimerLength(this, (taskExpectedTime).toLong())

        one_liner_quote.text = oneLinerQuotes.random()
        show_expected_time.text = resources.getString(R.string.expectedTime).format(taskExpectedTime)
        show_description.text = taskDescription
        act_show_stopped_time.visibility = View.INVISIBLE

        timer_done_btn.setOnClickListener {
            Toast.makeText(this, "Data acquired. Mark the task now!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }

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
            finishAndSaveTime()
        }
    }

    private fun finishAndSaveTime() {
        timerStopIn = initialTime - timerStopAt
        recordedTime = PrefUtil.getRecordedTime(this) + timerStopIn / 60L
        timeForGraph = PrefUtil.getWorkTimeForGraph(this) + recordedTime / 60f
        if (timerState == TimerState.Paused) onTimerFinished()
        else {
            timer.cancel()
            onTimerFinished()
        }
        showStopTime(timerStopIn)
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

    private fun onTimerFinished() {
        timerState = TimerState.Stopped
        setNewTimer()
        progress_ring.max = timerLength.toInt()
        progress_ring.progress = timerLength.toInt()
        PrefUtil.setTimeRemaining(this, timerLength)
        timeRemaining = timerLength
        timeRemaining = timerLength
        PrefUtil.setRecordingTime(
            this,
            recordedTime
        )  // Setting the recording time here, 'cause this fun gonna called in both conditions.
        PrefUtil.setWorkTimeForGraph(this, timeForGraph)
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        updateButtons()
        updateCountdownUI()
    }

    private fun showStopTime(stopIn: Long) {
        val showTime = "Stopped in: ${clockToShow(stopIn)}"
        act_show_stopped_time.text = showTime
        act_show_stopped_time.visibility = View.VISIBLE
    }


    // TIMER
    private fun startTimer() {
        timerState = TimerState.Running
        PrefUtil.setTimerName(this, taskTitle)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        timer = object : CountDownTimer(timeRemaining * 1000, 1000) {
            override fun onFinish() {
                recordedTime =
                    PrefUtil.getRecordedTime(this@TimerActivity) + timerLength / 60  // if timer completed, the recorded time will be the expected time for the first.
                timeForGraph = PrefUtil.getWorkTimeForGraph(this@TimerActivity) + recordedTime / 60f
                onTimerFinished()
            }

            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished < 15 * 1000) MainActivity.makeSound(applicationContext, R.raw.tick_sound)
                timeRemaining = millisUntilFinished / 1000
                timerStopAt = timeRemaining
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimer() {
        val lengthInMin = PrefUtil.getTimerLength(this)
        timerLength = (lengthInMin * 60L)
        progress_ring.max = timerLength.toInt()
    }

    private fun setPreviousTimer() {
        timerLength = PrefUtil.getPreviousTimerLength(this)
        progress_ring.max = timerLength.toInt()
    }

    private fun updateCountdownUI() {
        timer_countdown.text = clockToShow(timeRemaining)
        progress_ring.progress = (timeRemaining).toInt()
    }

    private fun clockToShow(time: Long): String {
        minsUntilFinished = time / 60
        secsInMinsUntilFinished = time - minsUntilFinished * 60
        secStr = secsInMinsUntilFinished.toString()
        return "$minsUntilFinished:${if (secStr.length == 2) secStr else "0$secStr"}"
    }

    private fun updateButtons() {
        when (timerState) {
            TimerState.Running -> {
                timer_done_btn.hide()
                fab_play.hide()
                fab_pause.show()
                fab_stop.show()
            }
            TimerState.Stopped -> {
                fab_play.show()
                fab_pause.hide()
                fab_stop.hide()
                timer_done_btn.show()
            }
            TimerState.Paused -> {
                timer_done_btn.hide()
                fab_play.show()
                fab_stop.show()
                fab_pause.hide()
            }
        }
    }
}
