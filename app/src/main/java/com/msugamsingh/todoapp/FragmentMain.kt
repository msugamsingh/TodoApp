package com.msugamsingh.todoapp

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.Toast
import com.msugamsingh.todoapp.db.TaskDBTable
import com.msugamsingh.todoapp.util.PrefUtil
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class FragmentMain : Fragment(), TaskAdapter.TaskClickListener {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskDBTable: TaskDBTable
    private var getTimerStopTime: Long = 0

    companion object {
        fun changeFragment(fragmentManager: FragmentManager, fragment: Fragment) {
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment).commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        taskAdapter = TaskAdapter(
            TaskDBTable(context!!).getIncompleteTasks(),
            this
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v =  inflater.inflate(R.layout.fragment_main, container, false)
        if (taskAdapter.itemCount == 0) v.tv_no_task.visibility = View.VISIBLE
        taskDBTable = TaskDBTable(context!!)
        getTimerStopTime = PrefUtil.getRecordedTime(context!!)
        return v
    }

    override fun onStart() {
        super.onStart()
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(context!!)
        refreshList()
    }

    private fun firstTaskFirst() {
        // Getting the First Task
        if (taskAdapter.itemCount > 0) {
            val firstTask = taskAdapter.getFirstTask()
            // FirstTaskFirst
            PrefUtil.setTaskPref(context!!, firstTask.title, firstTask.description, firstTask.expectedTime)
            PrefUtil.setRecordingTime(context!!, 0)
            changeFragment(fragmentManager!!, FragmentTimer())
        } else {
            PrefUtil.setTaskPref(context!!, "", "", 0)
            Toast.makeText(context!!, "No Task Left", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshList() {
        rv.adapter = TaskAdapter(
            TaskDBTable(context!!).getIncompleteTasks(),
            this
        )
        if (taskAdapter.itemCount == 0) tv_no_task.visibility = View.VISIBLE
    }

    private fun refreshLayout() = changeFragment(fragmentManager!!, FragmentMain())

    override fun onResume() {
        refreshList()
        getTimerStopTime = PrefUtil.getRecordedTime(context!!)
        super.onResume()
    }

    override fun onTaskClick(position: Int) {
        val task = taskAdapter.getTask(position)
        if (PrefUtil.getTimerState(context!!) == TimerActivity.TimerState.Stopped || PrefUtil.getTimerName(context!!) == task.title) {
            if (PrefUtil.getTimerName(context!!) == task.title) {
                val fragmentTimer = FragmentTimer()
                val bundle = Bundle()
                bundle.putParcelable(TASK_OBJECT, task)
                fragmentTimer.arguments = bundle
                changeFragment(fragmentManager!!, fragmentTimer)
            } else {
                PrefUtil.setRecordingTime(context!!, 0)
                Log.d("TimeCalculation", "onTaskClick(): recorded time pref: ${PrefUtil.getRecordedTime(context!!)}")
                val fragmentTimer = FragmentTimer()
                val bundle = Bundle()
                bundle.putParcelable(TASK_OBJECT, task)
                fragmentTimer.arguments = bundle
                changeFragment(fragmentManager!!, fragmentTimer)
            }
        }
        else Toast.makeText(
            context!!,
            "You can't open any task while you are in between a task.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onTaskDelete(position: Int) {
        val task = taskAdapter.getTask(position)
        val dialog = AlertDialog.Builder(context!!)
        dialog.setTitle("Are you sure?")
        dialog.setMessage("On continue, the task will be deleted.")
        dialog.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
            taskDBTable.deleteTask(task)
            refreshLayout()
        }
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int -> }
        dialog.show()
    }

    override fun onCheckBoxTap(position: Int) {
        val task = taskAdapter.getTask(position)
        if (task.title == PrefUtil.getTimerName(context!!)) {
            task.isCompleted = 1
            task.doneTime = getTimerStopTime.toInt()
            taskDBTable.setIsComplete(task)
            taskDBTable.updateTaskDoneTime(task, getTimerStopTime)
            PrefUtil.setRecordingTime(context!!, 0)
        } else {
            task.isCompleted = 1
            task.doneTime = 0
            taskDBTable.setIsComplete(task)
            taskDBTable.updateTaskDoneTime(task, 0)
        }
        Log.d("checkbox", "position is $position")
        MainActivity.makeSound(context!!, R.raw.drop)
        Toast.makeText(context!!, "Task done", Toast.LENGTH_SHORT).show()
        refreshLayout()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh_items -> {
                refreshLayout()
                true
            }
            R.id.menu_set_first_task-> {
                firstTaskFirst()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
