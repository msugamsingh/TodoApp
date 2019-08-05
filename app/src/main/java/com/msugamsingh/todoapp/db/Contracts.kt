package com.msugamsingh.todoapp.db

import android.provider.BaseColumns

const val DATABASE_NAME = "taskapp.db"
const val DATABASE_VERSION = 10

object TaskEntry: BaseColumns {
    const val TABLE_NAME = "task"
    const val _ID = BaseColumns._ID
    const val TITLE_COL = "title"
    const val PRIORITY_COL = "priority"
    const val DESCRIPTION_COL = "description"
    const val EXPECTED_TIME_COL = "expected_time"
    const val DONE_TIME_COL = "done_time"
    const val IS_COMPLETED_COL = "is_completed"
}