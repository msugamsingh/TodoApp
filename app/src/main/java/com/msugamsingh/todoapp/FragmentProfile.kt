package com.msugamsingh.todoapp

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.msugamsingh.todoapp.util.PrefUtil
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class FragmentProfile : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_profile, container, false)

        v.settings_layout.visibility = View.GONE
        v.about_layout.visibility = View.GONE
        v.user_name.setText(PrefUtil.getUserName(context!!))
        v.stats.setOnClickListener {
            startActivity(Intent(context!!, StatsActivity::class.java))
        }

        v.settings.setOnClickListener {
            if (v.settings_layout.visibility == View.GONE ) {
                v.settings_layout.visibility = View.VISIBLE
            } else v.settings_layout.visibility = View.GONE
        }

        v.about.setOnClickListener {
            if (v.about_layout.visibility == View.GONE) {
                v.about_layout.visibility = View.VISIBLE
            } else v.about_layout.visibility = View.GONE
        }

        v.instruction.setOnClickListener {
            startActivity(Intent(context!!, InstructionsActivity::class.java))
        }


        v.dark_switch.isChecked = PrefUtil.getUIMode(context!!)

        v.dark_switch.setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
            when (PrefUtil.getUIMode(context!!)) {
                false -> {
                    // Set UI to Dark
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    PrefUtil.setUIMode(context!!, true)
                    activity!!.finish()
                    startActivity(Intent(context!!, MainActivity::class.java))
                }
                true -> {
                    // Set UI to Light
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    PrefUtil.setUIMode(context!!, false)
                    activity!!.finish()
                    startActivity(Intent(context!!, MainActivity::class.java))
                }
            }
        }

        v.quote_switch.isChecked = PrefUtil.getIfToShowQuote(context!!)

        v.quote_switch.setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
            when (PrefUtil.getIfToShowQuote(context!!)) {
                false -> PrefUtil.setIfToShowQuote(context!!, true)
                true -> PrefUtil.setIfToShowQuote(context!!, false)
            }
        }

        return v
    }


    override fun onPause() {
        super.onPause()
        if (user_name.text != null && user_name.text.isNotEmpty() ) {
            PrefUtil.setUserName(context!!, user_name.text.toString())
        }
    }
}