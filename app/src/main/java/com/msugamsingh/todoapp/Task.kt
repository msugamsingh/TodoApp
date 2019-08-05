package com.msugamsingh.todoapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class Priority {
    companion object {
        const val HIGH_PRIORITY = 1
        const val MEDIUM_PRIORITY = 2
        const val LOW_PRIORITY = 3
    }
}

@Parcelize
data class Task (val title: String, val description: String, val priority: Int, val expectedTime: Int, var doneTime: Int = 0, var isCompleted: Int = 0): Parcelable

