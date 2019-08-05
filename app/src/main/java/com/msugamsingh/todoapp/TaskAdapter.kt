package com.msugamsingh.todoapp

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.rv_completed_layout.view.*
import kotlinx.android.synthetic.main.rv_layout.view.*

class TaskViewHolder(var itemLayout: View, private val taskClickListener: TaskAdapter.TaskClickListener) : RecyclerView.ViewHolder(itemLayout), View.OnClickListener {

    init {
        itemLayout.setOnClickListener(this)
        itemLayout.del_task.setOnClickListener {
            taskClickListener.onTaskDelete(adapterPosition)
        }
        itemLayout.rv_task_title.setOnClickListener{
            taskClickListener.onCheckBoxTap(adapterPosition)
        }
    }

    override fun onClick(v: View) {
        Log.d("TaskViewHolder", "onClick of adapter called")
        taskClickListener.onTaskClick(adapterPosition)
    }

}

class TaskAdapter(var tasks: List<Task>, private val taskClickListener: TaskClickListener) : RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_layout, parent, false)
        return TaskViewHolder(view, taskClickListener)
    }

    override fun getItemCount(): Int = tasks.size

    fun getTask(position: Int): Task = tasks[position]

    fun getFirstTask() = tasks.first()

    override fun onBindViewHolder(holder: TaskViewHolder, index: Int) {
        holder.itemLayout.rv_task_title.text = tasks[index].title
        holder.itemLayout.rv_desc.text = tasks[index].description

        val rvPriority = holder.itemLayout.rv_priority
        when (tasks[index].priority) {

            Priority.LOW_PRIORITY -> rvPriority.setBackgroundResource(R.drawable.priority_low)

            Priority.HIGH_PRIORITY -> rvPriority.setBackgroundResource(R.drawable.priority_high)

            Priority.MEDIUM_PRIORITY -> rvPriority.setBackgroundResource(R.drawable.priority_medium)
        }
    }

    interface TaskClickListener {
        fun onTaskClick(position: Int)
        fun onTaskDelete(position: Int)
        fun onCheckBoxTap(position: Int)
    }
}

class CompletedTaskViewHolder(val itemLayout: View) : RecyclerView.ViewHolder(itemLayout)

class CompletedTaskAdapter(var tasks: List<Task>) : RecyclerView.Adapter<CompletedTaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CompletedTaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_completed_layout, parent, false)
        return CompletedTaskViewHolder(view)
    }

    override fun getItemCount(): Int = tasks.size

    private fun setResultTime(index: Int) : String {
        val task = tasks[index]
        val result = task.expectedTime - task.doneTime
        return if (result > 0) "+$result"
        else "$result"
    }

    override fun onBindViewHolder(holder: CompletedTaskViewHolder, index: Int) {
        val task = tasks[index]
        val resultTime = setResultTime(index)
        Log.d("TaskAdapter", "onBindViewHolder: result is $resultTime")
        with(holder.itemLayout) {
            completed_task_description.text = task.description
            completed_task_title.text = task.title
            completed_task_completed_time.text = resources.getString(R.string.completed_in).format(task.doneTime)
            completed_task_expected_time.text = resources.getString(R.string.expeted_time).format(task.expectedTime)
            time_result.text = resources.getString(R.string.time_result).format(setResultTime(index))
        }
    }
}
