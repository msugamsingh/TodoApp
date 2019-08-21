package com.msugamsingh.todoapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.msugamsingh.todoapp.util.PrefUtil
import kotlinx.android.synthetic.main.activity_stats.*
import java.util.*
import kotlin.math.roundToInt


class StatsActivity : AppCompatActivity() {

    private var dailyAverage = "0 h"
    private var weeklyTotalHours = "0 h"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        supportActionBar?.elevation = 0f
        supportActionBar?.title = "Statistics"
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.white_drawable))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
//        PrefUtil.setDayOfTheWeek(this, day)

        show_result_button.setOnClickListener {
            val name = PrefUtil.getUserName(this)
            show_result.text =
                "${if (name.isNotEmpty() || name.isNotBlank()) name else "Sir/Ma'am"}, You are " +
                        "currently working $dailyAverage" + "ours a day and you worked total $weeklyTotalHours" + "ours this week."

            if (show_result.visibility == View.VISIBLE) show_result.visibility = View.GONE
            else show_result.visibility = View.VISIBLE
        }

        val time = PrefUtil.getWorkTimeForGraph(this)

        when (day) {
            1 -> PrefUtil.setWorkTimeForSun(this, time)
            2 -> PrefUtil.setWorkTimeForMon(this, time)
            3 -> PrefUtil.setWorkTimeForTue(this, time)
            4 -> PrefUtil.setWorkTimeForWed(this, time)
            5 -> PrefUtil.setWorkTimeForThu(this, time)
            6 -> PrefUtil.setWorkTimeForFri(this, time)
            7 -> PrefUtil.setWorkTimeForSat(this, time)
        }

        val weekdayTimeList = mutableListOf(
            PrefUtil.getWorkTimeForSun(this),
            PrefUtil.getWorkTimeForMon(this),
            PrefUtil.getWorkTimeForTue(this),
            PrefUtil.getWorkTimeForWed(this),
            PrefUtil.getWorkTimeForThu(this),
            PrefUtil.getWorkTimeForFri(this),
            PrefUtil.getWorkTimeForSat(this)
        )

        dailyAverage = resources.getString(R.string.weeklyAverageValue).format(weekdayTimeList.average().roundToInt())
        weeklyTotalHours = resources.getString(R.string.weeklyAverageValue).format(weekdayTimeList.sum().roundToInt())

        weekly_average_value.text = dailyAverage
        weekly_total_hours.text = weeklyTotalHours

        val chart = mpgraph
        val entry = arrayListOf<BarEntry>()
        entry.add(BarEntry(0f, PrefUtil.getWorkTimeForSun(this)))
        entry.add(BarEntry(1f, PrefUtil.getWorkTimeForMon(this)))
        entry.add(BarEntry(2f, PrefUtil.getWorkTimeForTue(this)))
        entry.add(BarEntry(3f, PrefUtil.getWorkTimeForWed(this)))
        entry.add(BarEntry(4f, PrefUtil.getWorkTimeForThu(this)))
        entry.add(BarEntry(5f, PrefUtil.getWorkTimeForFri(this)))
        entry.add(BarEntry(6f, PrefUtil.getWorkTimeForSat(this)))


        val dataSet = BarDataSet(entry, "Hours")
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.lightTextGrey)

        // Bar Colors
        dataSet.color = ContextCompat.getColor(this, R.color.colorAccent)
        val labels = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        val chartDescription = Description()
        chartDescription.text = "Stats from last 7 days"
        chartDescription.textColor = ContextCompat.getColor(this, R.color.lightTextGrey)

        val data = BarData(dataSet)
        chart.data = data
        chart.description = chartDescription
        chart.animateY(800)

        val formatter = IndexAxisValueFormatter()
        formatter.values = labels
        val xis = chart.xAxis
        xis.valueFormatter = formatter
        xis.labelRotationAngle = 16f
        xis.textColor = ContextCompat.getColor(this, R.color.lightTextGrey)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()

    }

    override fun onStop() {
        super.onStop()
        show_result.visibility = View.GONE
    }
}











