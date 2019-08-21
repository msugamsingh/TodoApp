package com.msugamsingh.todoapp

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.msugamsingh.todoapp.util.PrefUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

const val TASK_OBJECT = "com.msugamsingh.todoapp.taskadapter"

class MainActivity(private val someFrag: Fragment = FragmentMain()) : AppCompatActivity() {

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
            R.id.action_profile -> {
                selectedFragment = FragmentProfile()
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

        supportActionBar?.title = "  4TR"
        supportActionBar?.subtitle = "   Productivity App"
        supportActionBar?.setIcon(R.drawable.ic_logo)

        if (PrefUtil.getUIMode(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, someFrag).commit()
        bottom_nav.setOnNavigationItemSelectedListener(navListener)

        fab.setOnClickListener {
            finish()
            startActivity(Intent(this, CreateTaskActivity::class.java))
        }


        // Next day, set the whole work time to 0
        val date = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        if (date != PrefUtil.getDate(this)) {
            PrefUtil.setDate(this, date)
            PrefUtil.setWorkTimeForGraph(this, 0f)
        }
    }

    override fun onBackPressed() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(
            "Do you want to exit?"
        )
        dialog.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            super.onBackPressed()
        }
        dialog.setNegativeButton("No") { _: DialogInterface, _: Int ->
        }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        PrefUtil.setIfQuoteShown(this, false)
    }
}

