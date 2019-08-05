package com.msugamsingh.todoapp

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

const val TASK_OBJECT = "com.msugamsingh.todoapp.taskadapter"

class MainActivity : AppCompatActivity() {

    companion object {

        fun makeSound(context: Context, soundId: Int) {
            val mp = MediaPlayer.create(context, soundId)
            mp.setOnCompletionListener {
                it.reset()
                it.release()
            }
            mp.start()
        }
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        lateinit var selectedFragment: Fragment
        when (item.itemId) {
            R.id.action_settings -> {
//                selectedFragment = TODO fragmentME
            }
            R.id.action_timer -> {
                selectedFragment = FragmentTimer()
            }
            R.id.action_todo -> {
                selectedFragment = FragmentMain()
            }
            R.id.action_completed -> {
                selectedFragment = FragmentCompletedTasks()
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, selectedFragment).commit()
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.drawable.ic_todo_check)
        supportActionBar?.title = "    Tasks"

        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, FragmentMain()).commit()
        bottom_nav.setOnNavigationItemSelectedListener(navListener)

        fab.setOnClickListener {
            startActivity(Intent(this, CreateTaskActivity::class.java))
        }
    }
}

