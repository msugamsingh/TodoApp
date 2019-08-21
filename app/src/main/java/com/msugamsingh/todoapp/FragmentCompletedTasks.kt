package com.msugamsingh.todoapp

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.msugamsingh.todoapp.db.TaskDBTable
import kotlinx.android.synthetic.main.fragment_completed_tasks.*
import kotlinx.android.synthetic.main.fragment_completed_tasks.view.*

class FragmentCompletedTasks : Fragment() {

    private lateinit var taskAdapter: CompletedTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        taskAdapter = CompletedTaskAdapter(
            TaskDBTable(context!!).getCompletedTasks()
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_completed_tasks, container, false)

        v.clear_all.setOnClickListener {
            val dialog = AlertDialog.Builder(context!!)
            dialog.setTitle("Clear All?")
            dialog.setMessage("This will clear all completed tasks.")
            dialog.setPositiveButton("Clear All") { _: DialogInterface, _: Int ->
                TaskDBTable(context!!).clearAllCompleted()
                MainActivity.makeSound(context!!, R.raw.woosh_sound)
                refreshLayout()
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int -> }
            dialog.show()
        }

        if (taskAdapter.itemCount == 0) v.tv_no_completed_task.visibility = View.VISIBLE
        return v
    }

    override fun onStart() {
        super.onStart()
        rv_completed_tasks.setHasFixedSize(true)
        rv_completed_tasks.layoutManager = LinearLayoutManager(context!!)
        refreshCompletedList()
    }

    private fun refreshLayout() = FragmentMain.changeFragment(fragmentManager!!, FragmentCompletedTasks())

    private fun refreshCompletedList() {
        rv_completed_tasks.adapter = CompletedTaskAdapter(
            TaskDBTable(context!!).getCompletedTasks()
        )
    }

    override fun onResume() {
        refreshCompletedList()
        super.onResume()
    }

}
