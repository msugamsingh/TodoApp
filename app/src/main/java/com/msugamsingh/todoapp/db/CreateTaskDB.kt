package com.msugamsingh.todoapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.msugamsingh.todoapp.db.TaskEntry.DESCRIPTION_COL
import com.msugamsingh.todoapp.db.TaskEntry.DONE_TIME_COL
import com.msugamsingh.todoapp.db.TaskEntry.EXPECTED_TIME_COL
import com.msugamsingh.todoapp.db.TaskEntry.IS_COMPLETED_COL
import com.msugamsingh.todoapp.db.TaskEntry.PRIORITY_COL
import com.msugamsingh.todoapp.db.TaskEntry.TABLE_NAME
import com.msugamsingh.todoapp.db.TaskEntry.TITLE_COL
import com.msugamsingh.todoapp.db.TaskEntry._ID

private const val completedDefault = 0

class CreateTaskDB(context: Context) : SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION) {

    private val SQL_CREATE_ENTRIES = """
            CREATE TABLE $TABLE_NAME ($_ID INTEGER PRIMARY KEY,
            $TITLE_COL TEXT,
            $DESCRIPTION_COL TEXT,
            $PRIORITY_COL INTEGER,
            $EXPECTED_TIME_COL INTEGER,
            $DONE_TIME_COL INTEGER,
            $IS_COMPLETED_COL INTEGER Default $completedDefault)
        """.trimIndent()

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
    }
}

