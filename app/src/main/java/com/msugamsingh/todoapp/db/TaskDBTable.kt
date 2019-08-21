package com.msugamsingh.todoapp.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.msugamsingh.todoapp.Task
import com.msugamsingh.todoapp.db.TaskEntry.DESCRIPTION_COL
import com.msugamsingh.todoapp.db.TaskEntry.DONE_TIME_COL
import com.msugamsingh.todoapp.db.TaskEntry.EXPECTED_TIME_COL
import com.msugamsingh.todoapp.db.TaskEntry.IS_COMPLETED_COL
import com.msugamsingh.todoapp.db.TaskEntry.PRIORITY_COL
import com.msugamsingh.todoapp.db.TaskEntry.TABLE_NAME
import com.msugamsingh.todoapp.db.TaskEntry.TITLE_COL
import com.msugamsingh.todoapp.db.TaskEntry._ID

class TaskDBTable(context: Context) {
    private val dbHelper = CreateTaskDB(context)

    fun storeTask(task: Task): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        with(values) {
            put(TITLE_COL, task.title)
            put(DESCRIPTION_COL, task.description)
            put(PRIORITY_COL, task.priority)
            put(EXPECTED_TIME_COL, task.expectedTime)
            put(DONE_TIME_COL, task.doneTime)
            put(IS_COMPLETED_COL, task.isCompleted)
        }
        return db.transaction {
            insert(TABLE_NAME, null, values)
        }
    }

    fun doesTitleAlreadyExists(title: String) : Boolean {
        val db = dbHelper.readableDatabase
        try {
            db.rawQuery("SELECT $TITLE_COL FROM $TABLE_NAME WHERE $TITLE_COL =? AND $IS_COMPLETED_COL =?", arrayOf(title, "0")).use { cursor -> return cursor.count > 0 }
        } catch (e: Exception) {
            return false
        } finally {
            db.close()
        }
    }

    fun updateTaskDoneTime(task: Task, time: Long): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(DONE_TIME_COL, time)

        return db.transaction {
            update(TABLE_NAME, values, "$TITLE_COL=? AND $DESCRIPTION_COL=?", arrayOf(task.title, task.description))
        }
    }

    fun clearAllCompleted() {
        val db = dbHelper.writableDatabase
        db.transaction {
            delete(TABLE_NAME, "$IS_COMPLETED_COL=?", arrayOf("1"))
        }
    }

    fun setIsComplete(task: Task): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(IS_COMPLETED_COL, 1)

        return db.transaction {
            update(TABLE_NAME, values, "$TITLE_COL=? AND $DESCRIPTION_COL=?", arrayOf(task.title, task.description))
        }
    }

    fun deleteTask(task: Task) {
        val db = dbHelper.writableDatabase
        db.transaction {
            delete(TABLE_NAME, "$TITLE_COL=? AND $DESCRIPTION_COL=?", arrayOf(task.title, task.description))
        }
    }

    fun getIncompleteTasks(): List<Task> {
        val cols = arrayOf(_ID, TITLE_COL, DESCRIPTION_COL, PRIORITY_COL, EXPECTED_TIME_COL, DONE_TIME_COL, IS_COMPLETED_COL)
        val order = "$PRIORITY_COL ASC"         // DESC
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_NAME, cols, "$IS_COMPLETED_COL=?", arrayOf("0"), null, null, order)
        val tasks = mutableListOf<Task>()
        while (cursor.moveToNext()) {
            val title =cursor.getString(cursor.getColumnIndex(TITLE_COL))
            val description = cursor.getString(cursor.getColumnIndex(DESCRIPTION_COL))
            val priority = cursor.getInt(cursor.getColumnIndex(PRIORITY_COL))
            val  expectedTime = cursor.getInt(cursor.getColumnIndex(EXPECTED_TIME_COL))
            val doneTime = cursor.getInt(cursor.getColumnIndex(DONE_TIME_COL))
            val isCompleted = cursor.getInt(cursor.getColumnIndex(IS_COMPLETED_COL))
            tasks.add(Task(title, description, priority, expectedTime, doneTime, isCompleted))
        }
        cursor.close()
        return tasks
    }

    fun getCompletedTasks() : List<Task> {
        val cols = arrayOf(_ID, TITLE_COL, DESCRIPTION_COL, PRIORITY_COL, EXPECTED_TIME_COL, DONE_TIME_COL, IS_COMPLETED_COL)
        val order = "$DONE_TIME_COL DESC"         // DESC
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_NAME, cols, "$IS_COMPLETED_COL=?", arrayOf("1"), null, null, order)

        val tasks = mutableListOf<Task>()
        while (cursor.moveToNext()) {
            val title =cursor.getString(cursor.getColumnIndex(TITLE_COL))
            val description = cursor.getString(cursor.getColumnIndex(DESCRIPTION_COL))
            val priority = cursor.getInt(cursor.getColumnIndex(PRIORITY_COL))
            val  expectedTime = cursor.getInt(cursor.getColumnIndex(EXPECTED_TIME_COL))
            val doneTime = cursor.getInt(cursor.getColumnIndex(DONE_TIME_COL))
            val isCompleted = cursor.getInt(cursor.getColumnIndex(IS_COMPLETED_COL))
            tasks.add(Task(title, description, priority, expectedTime, doneTime, isCompleted))
        }
        cursor.close()
        return tasks
    }
}

private inline fun <T> SQLiteDatabase.transaction(function: SQLiteDatabase.() -> T): T {
    this.beginTransaction()
    //    close() i moved this to finally block
    return try {
        val returnValue = function()
        setTransactionSuccessful()
        returnValue
    } finally {
        endTransaction()
        close()
    }
}