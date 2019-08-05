package com.msugamsingh.todoapp

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityOptions
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.*
import android.widget.Toast
import com.msugamsingh.todoapp.TimerActivity.TimerState.*
import com.msugamsingh.todoapp.util.NotificationUtil
import com.msugamsingh.todoapp.util.PrefUtil
import kotlinx.android.synthetic.main.fragment_timer.*
import kotlinx.android.synthetic.main.fragment_timer.view.*
import java.util.*


class FragmentTimer : Fragment() {

    companion object {
        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmTime(context, nowSeconds)
            return wakeUpTime
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000

        fun removeAlarm(context: Context) {
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmTime(context, 0)
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun slideTransition(context: Context, activity: Activity) {
            val options = ActivityOptions.makeSceneTransitionAnimation(activity)

            val intent = Intent(context, SettingsActivity::class.java)
            intent.putExtra("Anim_Key", "slideXML")
            ContextCompat.startActivity(context, intent, options.toBundle())
        }
    }

    private lateinit var timer: CountDownTimer
    private var timerLength = 0L
    private var timerState = Stopped
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_timer, container, false)
        if (PrefUtil.getTimerState(context!!) == Running || PrefUtil.getTimerState(context!!) == Paused) {
            val task = PrefUtil.getTaskPref(context!!)
            taskTitle = task[0].toString()
            taskDescription = task[2].toString()
            taskExpectedTime = task[1].toString().toInt()
        } else {
            if (arguments != null) {
                task = arguments!!.getParcelable(TASK_OBJECT)
                taskTitle = task.title
                taskDescription = task.description
                taskExpectedTime = task.expectedTime
            } else {
                val task = PrefUtil.getTaskPref(context!!)
                taskTitle = task[0].toString()
                taskDescription = task[2].toString()
                taskExpectedTime = task[1].toString().toInt()
            }
            PrefUtil.setTaskPref(context!!, taskTitle, taskDescription, taskExpectedTime)
        }

        with(v) {
            frag_one_liner_quote.text = oneLinerQuotes.random()
            frag_show_task_title.text = taskTitle
            frag_show_expected_time.text = taskExpectedTime.toString()
            frag_show_description.text = taskDescription
            show_stopped_time.visibility = View.INVISIBLE
        }


        v.frag_timer_done_btn.setOnClickListener {
            Toast.makeText(context!!, "Data taken. Check the task now!", Toast.LENGTH_SHORT).show()
            FragmentMain.changeFragment(fragmentManager!!, FragmentMain())
        }

        PrefUtil.setTimerLength(context!!, (taskExpectedTime).toLong())

        return v
    }

    override fun onStart() {
        super.onStart()
        initialTime = PrefUtil.getTimerLength(context!!) * 60L

        frag_fab_play.setOnClickListener {
            startTimer()
            timerState = Running
            updateButtons()
        }


        frag_fab_pause.setOnClickListener {
            timer.cancel()
            timerState = Paused
            updateButtons()
        }

        frag_fab_stop.setOnClickListener {
            finishAndSaveTime()
        }
    }

    override fun onResume() {
        super.onResume()

        initTimer()
        removeAlarm(context!!)
        NotificationUtil.hideTimerNotification(context!!)
    }

    private fun finishAndSaveTime() {
        timerStopIn = initialTime - timerStopAt
        recordedTime = PrefUtil.getRecordedTime(context!!) + timerStopIn/60L
        Log.d("TimeCalculation", "finishAndSaveTime(): recorded time: $recordedTime, pref: ${PrefUtil.getRecordedTime(context!!)}")
        if (timerState == Paused) onTimerFinished()
        else {
            timer.cancel()
            onTimerFinished()
        }
        showStopTime(timerStopIn)
    }


    override fun onPause() {
        super.onPause()

        if (timerState == Running) {
            timer.cancel()
            val wakeUpTime = setAlarm(context!!, nowSeconds, timeRemaining)
            NotificationUtil.showTimerRunning(context!!, wakeUpTime)
        } else if (timerState == Paused) {
            NotificationUtil.showTimerPaused(context!!)
        }

        PrefUtil.setPreviousTimerLength(context!!, timerLength)
        PrefUtil.setTimeRemaining(context!!, timeRemaining)
        PrefUtil.setTimerState(context!!, timerState)
    }

    private fun initTimer() {
        timerState = PrefUtil.getTimerState(context!!)


        if (timerState == Stopped) setNewTimer()
        else setPreviousTimer()

        timeRemaining = if (timerState == Running || timerState == Paused)
            PrefUtil.getTimeRemaining(context!!)
        else timerLength

        val alarmSetTime = PrefUtil.getAlarmTime(context!!)
        if (alarmSetTime > 0) timeRemaining -= nowSeconds - alarmSetTime

        if (timeRemaining <= 0) onTimerFinished()
        else if (timerState == Running) startTimer()
        updateButtons()
        updateCountdownUI()
    }

    private fun onTimerFinished() {
        timerState = Stopped
        setNewTimer()
        frag_progress_ring.max = timerLength.toInt()
        frag_progress_ring.progress = timerLength.toInt()
        PrefUtil.setTimeRemaining(context!!, timerLength)
        timeRemaining = timerLength
        Log.d("TimeCalculation", "onTimerFinished(): recorded time: $recordedTime, pref: ${PrefUtil.getRecordedTime(context!!)}")
        PrefUtil.setRecordingTime(context!!, recordedTime)  // Setting the recording time here, 'cause this fun gonna called in both conditions.
        Log.d("TimeCalculation", "onTimerFinished(): recorded time: $recordedTime, pref: ${PrefUtil.getRecordedTime(context!!)}")
        updateButtons()
        updateCountdownUI()
    }

    private fun showStopTime(stopIn: Long) {
        val showTime = "Stopped in: ${clockToShow(stopIn)}"
        show_stopped_time.text = showTime
        show_stopped_time.visibility = View.VISIBLE
    }

    // TIMER
    private fun startTimer() {
        timerState = Running
        PrefUtil.setTimerName(context!!, taskTitle)

        timer = object : CountDownTimer(timeRemaining * 1000, 1000) {
            override fun onFinish() {
                recordedTime = PrefUtil.getRecordedTime(context!!) + timerLength/60  // if timer completed, the recorded time will be the expected time for the first.
                Log.d("TimeCalculation", "startTimer(): recorded time: $recordedTime, pref: ${PrefUtil.getRecordedTime(context!!)}")
                onTimerFinished()
            }

            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished < 15 * 1000) MainActivity.makeSound(context!!, R.raw.tick_sound)
                timeRemaining = millisUntilFinished / 1000
                timerStopAt = timeRemaining
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimer() {
        val lengthInMin = PrefUtil.getTimerLength(context!!)
        timerLength = (lengthInMin * 60L)

        frag_progress_ring.max = timerLength.toInt()
    }

    private fun setPreviousTimer() {
        timerLength = PrefUtil.getPreviousTimerLength(context!!)
        frag_progress_ring.max = timerLength.toInt()
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountdownUI() {
        Log.d("TimerActivity", "updateUI called with $timeRemaining")

        frag_timer_countdown.text = clockToShow(timeRemaining)
        frag_progress_ring.progress = (timeRemaining).toInt()
    }

    private fun clockToShow(time: Long): String {
        minsUntilFinished = time / 60
        secsInMinsUntilFinished = time - minsUntilFinished * 60
        secStr = secsInMinsUntilFinished.toString()
        return "$minsUntilFinished:${if (secStr.length == 2) secStr else "0$secStr"}"
    }

    private fun updateButtons() {
        when (timerState) {
            Running -> {
                frag_timer_done_btn.hide()
                frag_fab_play.hide()
                frag_fab_pause.show()
                frag_fab_stop.show()
            }
            Stopped -> {
                frag_fab_play.show()
                frag_fab_pause.hide()
                frag_fab_stop.hide()
                frag_timer_done_btn.show()
            }
            Paused -> {
                frag_timer_done_btn.hide()
                frag_fab_play.show()
                frag_fab_stop.show()
                frag_fab_pause.hide()
            }
        }
    }

    private fun refreshLayout() = FragmentMain.changeFragment(fragmentManager!!, FragmentTimer())


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.refresh_timer, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh_timer_details -> {
                PrefUtil.setTaskPref(context!!, "_ _", "_ _", 0)
                refreshLayout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
