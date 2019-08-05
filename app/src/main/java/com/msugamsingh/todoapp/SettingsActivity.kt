package com.msugamsingh.todoapp

import android.annotation.TargetApi
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.Window
import android.widget.SeekBar
import com.msugamsingh.todoapp.util.PrefUtil
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    var timeFromSeekBar: Int = 10


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.elevation = 0f
        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initAnim()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun initAnim() {
        val enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide_transition)
        window.enterTransition = enterTransition
    }

    override fun onSupportNavigateUp(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) finishAfterTransition()
        else finish()
        return true
    }
}
