package com.msugamsingh.todoapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import com.msugamsingh.todoapp.db.TaskDBTable
import com.msugamsingh.todoapp.util.PrefUtil
import kotlinx.android.synthetic.main.activity_create_task.*

class CreateTaskActivity : AppCompatActivity() {

    var priorityFromSpinner = Priority.HIGH_PRIORITY
    var expectedTimerFromSeekBar = 25
    var ifMultipleOptionIsOn = false
    val TAG = CreateTaskActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)
        supportActionBar?.elevation = 0f
        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.white_drawable))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        should_be_done_in.text = resources.getString(R.string.showExpectedTime).format(expectedTimerFromSeekBar)
        tv_error.visibility = View.INVISIBLE

        tv_quotes.text = quotesList.random().toString()
        val dropdown = task_priority
        val options = listOf("High", "Medium", "Low")

        dropdown.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options)
        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                priorityFromSpinner = Priority.HIGH_PRIORITY
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                priorityFromSpinner = when (options[position]) {
                    "High" -> Priority.HIGH_PRIORITY
                    "Medium" -> Priority.MEDIUM_PRIORITY
                    "Low" -> Priority.LOW_PRIORITY
                    else -> Priority.HIGH_PRIORITY
                }
            }
        }

        ifMultipleOptionIsOn = PrefUtil.getIfMultipleOptionIsOn(this)
        if (ifMultipleOptionIsOn) multiple_task_option.setTextColor(resources.getColor(R.color.colorAccent))


        multiple_task_option.setOnClickListener {
            if (ifMultipleOptionIsOn) {
                PrefUtil.setIfMultipleOptionIsOn(this, false)
                multiple_task_option.setTextColor(resources.getColor(R.color.lightTextGrey))
                ifMultipleOptionIsOn = false
            } else {
                PrefUtil.setIfMultipleOptionIsOn(this, true)
                multiple_task_option.setTextColor(resources.getColor(R.color.colorAccent))
                ifMultipleOptionIsOn = true
            }
        }


        task_expected_time.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                expectedTimerFromSeekBar = progress
                should_be_done_in.text = resources.getString(R.string.showExpectedTime).format(expectedTimerFromSeekBar)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Nothing to do here
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Nothing to do here
            }
        })
    }

    fun onSaveTask(v: View) {

        if (validationDone()) {
            // Store Task
            val title = task_title.text.toString().trim().replace("\\s+".toRegex(), " ")
            val description = task_description.text.toString().trim().replace("\\s+".toRegex(), " ")
            val expectedTime = expectedTimerFromSeekBar
            val priority = priorityFromSpinner
            val task = Task(title, description, priority, expectedTime, 0)

            // inserting to table always return id or -1(long) if something went wrong
            val id = TaskDBTable(this).storeTask(task)
            if (id == -1L) {
                tv_error.text = "Something went wrong :("
                tv_error.visibility = View.VISIBLE
            } else {
                tv_error.visibility = View.INVISIBLE
                if (ifMultipleOptionIsOn) {
                    task_title.setText("")
                    task_description.setText("")
                } else {
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
                Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validationDone(): Boolean {
        return if (task_title.text == null || task_title.text.trim().isEmpty()) {
            displayErrorMsg("Please set the title for the task.")
        } else if (task_description.text == null || task_description.text.trim().isEmpty()) {
            displayErrorMsg("Description: Just write the impact it would have on your day or life if it's done.") // TODO check english
        } else if (TaskDBTable(this).doesTitleAlreadyExists(task_title.text.toString())) {
            displayErrorMsg("Task Already Exists.")
        }
        else true
    }

    private fun displayErrorMsg(msg: String): Boolean {
        tv_error.text = msg
        tv_error.visibility = View.VISIBLE
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        onBackPressed()
        return true
    }


}
