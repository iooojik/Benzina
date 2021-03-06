package com.kirovcompany.benzina.interfaces

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.kirovcompany.benzina.R
import com.kirovcompany.benzina.StaticVars
import com.kirovcompany.benzina.localdb.AppDatabase
import com.kirovcompany.benzina.localdb.petrol.PetrolModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

interface ChartsUtil : FragmentUtil {

    fun showRoutesPerDayGraphic(
        database: AppDatabase, bottomView: View, activity: Activity,
        invalidate: Boolean, range: Int = 0, textViewAll: TextView
    ) {

        val models = database.routesPerDayModel().getAll()

        if (!models.isNullOrEmpty()){

            val values : ArrayList<Entry> = arrayListOf()
            val dates : ArrayList<String> = arrayListOf()

            var topValue = 0.0.toDouble()

            for (i in models.indices){


                if (invalidate){

                    val modelDate = SimpleDateFormat("dd.MM.yyyy").parse(models[i].date)
                    val cDate = SimpleDateFormat("dd.MM.yyyy").parse(getCurrentDate())


                    if (abs(daysBetween(modelDate, cDate)) <= range){

                        values.add(
                            Entry(
                                i.toFloat(), (models[i].num).toInt().toFloat()
                            )
                        )

                        topValue+=models[i].num

                        dates.add(models[i].date)
                    }

                } else {
                    values.add(
                        Entry(
                            i.toFloat(), (models[i].num).toInt().toFloat()
                        )
                    )
                    topValue+=models[i].num
                    dates.add(models[i].date)
                }
            }

            textViewAll.text = (topValue/models.size).toInt().toString()

            val chart = bottomView.findViewById<LineChart>(R.id.routes_chart)

            chart.invalidate()

            chart.description.isEnabled = false

            chart.legend.isEnabled = false
            chart.legend.textColor = Color.parseColor("#2a2d43")
            chart.legend.textSize = 14f

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(dates)
            chart.xAxis.textColor = Color.WHITE
            chart.xAxis.setDrawGridLines(false)
            chart.xAxis.granularity = 1f


            //chart.axisLeft.textColor = Color.WHITE
            chart.axisLeft.isEnabled = true
            chart.axisLeft.setDrawGridLines(true)
            chart.axisLeft.gridColor = Color.parseColor("#757575")
            chart.axisLeft.textColor = Color.parseColor("#757575")

            chart.axisRight.isEnabled = false
            chart.axisRight.textColor = Color.WHITE


            val lineDataSet = LineDataSet(values, "???????????????????? ?????????????????????? ?????????????? ?? ????????")

            lineDataSet.valueTextColor = Color.parseColor("#2a2d43")
            lineDataSet.valueTextSize = 14f
            lineDataSet.disableDashedLine()
            lineDataSet.lineWidth = 3f
            lineDataSet.setDrawFilled(true)
            lineDataSet.fillColor = Color.parseColor("#B15DFF")
            lineDataSet.setDrawHorizontalHighlightIndicator(false)
            lineDataSet.setDrawValues(false)

            val dataSets : ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(lineDataSet)

            chart.data = LineData(dataSets)

            activity.runOnUiThread {
                chart.animateY(500)
                chart.animateX(200)
            }

        }  else {
            bottomView.findViewById<LineChart>(R.id.routes_chart).visibility = View.GONE
        }
    }

    fun showRateGraphic(
        database: AppDatabase, bottomView: View, activity: Activity,
        invalidate: Boolean, range: Int = 0, textViewAll: TextView
    ){
        val models = database.routesPerDayModel().getAll()
        if (!models.isNullOrEmpty()){

            val values : ArrayList<BarEntry> = arrayListOf()
            val dates : ArrayList<String> = arrayListOf()
            var topVal = 0.0.toDouble()

            for (i in models.indices){


                if (invalidate){

                    val modelDate = SimpleDateFormat("dd.MM.yyyy").parse(models[i].date)
                    val cDate = SimpleDateFormat("dd.MM.yyyy").parse(getCurrentDate())


                    if (abs(daysBetween(modelDate, cDate)) <= range){

                        values.add(
                            BarEntry(
                                i.toFloat(), (models[i].averageCarRate).toFloat()
                            )
                        )

                        topVal += models[i].averageCarRate
                        dates.add(models[i].date)

                    }

                } else {
                    values.add(
                        BarEntry(
                            i.toFloat(), (models[i].averageCarRate).toFloat()
                        )
                    )
                    topVal += models[i].averageCarRate
                    dates.add(models[i].date)
                }
            }


            textViewAll.text = roundDouble(topVal).toString()

            val chart = bottomView.findViewById<BarChart>(R.id.rate_chart)
            chart.setFitBars(true)
            chart.description.isEnabled = false


            chart.setPinchZoom(false)
            chart.isDoubleTapToZoomEnabled = false

            chart.legend.isEnabled = false
            chart.legend.textColor = Color.parseColor("#2a2d43")
            chart.legend.textSize = 14f

            //chart.xAxis.textColor = Color.WHITE
            chart.xAxis.setDrawGridLines(false)
            chart.xAxis.granularity = 1f
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(dates)
            chart.xAxis.textColor = Color.WHITE


            //chart.axisLeft.textColor = Color.WHITE
            chart.axisLeft.isEnabled = true
            chart.axisLeft.setDrawGridLines(false)
            chart.axisLeft.gridColor = Color.parseColor("#757575")
            chart.axisLeft.textColor = Color.parseColor("#757575")

            chart.axisRight.isEnabled = false


            val barDataSet = BarDataSet(values, "???????????????????? ?????????????????????? ?????????????? ?? ????????")

            barDataSet.setDrawValues(false)

            val data = BarData(barDataSet)
            chart.data = data
            chart.invalidate()

            activity.runOnUiThread {
                chart.animateY(500)
                chart.animateX(200)
            }
        }  else {
            bottomView.findViewById<BarChart>(R.id.rate_chart).visibility = View.GONE
        }
    }

    fun showAverageSpeed(
        database: AppDatabase, bottomView: View, activity: Activity,
        invalidate: Boolean, range: Int = 0, textViewAll: TextView
    ){
        val models = database.routesPerDayModel().getAll()
        if (!models.isNullOrEmpty()){

            val values : ArrayList<BarEntry> = arrayListOf()
            val dates : ArrayList<String> = arrayListOf()

            var topValue = 0.0.toDouble()

            for (i in models.indices){


                if (invalidate){

                    val modelDate = SimpleDateFormat("dd.MM.yyyy").parse(models[i].date)
                    val cDate = SimpleDateFormat("dd.MM.yyyy").parse(getCurrentDate())


                    if (abs(daysBetween(modelDate, cDate)) <= range){

                        values.add(
                            BarEntry(
                                i.toFloat(), (models[i].averageSpeed).toInt().toFloat()
                            )
                        )
                        topValue+=models[i].averageSpeed
                        dates.add(models[i].date)
                    }

                } else {
                    values.add(
                        BarEntry(
                            i.toFloat(), (models[i].averageSpeed).toInt().toFloat()
                        )
                    )
                    topValue+=models[i].averageSpeed
                    dates.add(models[i].date)
                }
            }

            textViewAll.text = (topValue/values.size).toInt().toString()


            val chart = bottomView.findViewById<BarChart>(R.id.speed_chart)
            chart.setFitBars(true)
            chart.description.isEnabled = false


            chart.setPinchZoom(false)
            chart.isDoubleTapToZoomEnabled = false

            chart.legend.isEnabled = false
            chart.legend.textColor = Color.parseColor("#2a2d43")
            chart.legend.textSize = 14f

            //chart.xAxis.textColor = Color.WHITE
            chart.xAxis.setDrawGridLines(false)
            chart.xAxis.granularity = 1f
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(dates)
            chart.xAxis.textColor = Color.WHITE


            //chart.axisLeft.textColor = Color.WHITE
            chart.axisLeft.isEnabled = true
            chart.axisLeft.setDrawGridLines(false)
            chart.axisLeft.gridColor = Color.parseColor("#757575")
            chart.axisLeft.textColor = Color.parseColor("#757575")

            chart.axisRight.isEnabled = false


            val barDataSet = BarDataSet(values, "???????????????????? ?????????????????????? ?????????????? ?? ????????")

            barDataSet.setDrawValues(false)

            val data = BarData(barDataSet)
            chart.data = data
            chart.invalidate()

            activity.runOnUiThread {
                chart.animateY(500)
                chart.animateX(200)
            }
        }  else {
            bottomView.findViewById<BarChart>(R.id.rate_chart).visibility = View.GONE
        }
    }

    fun showDistance(
        database: AppDatabase, bottomView: View, activity: Activity,
        invalidate: Boolean, range: Int = 0, textViewAll: TextView
    ){

        val models = database.routesPerDayModel().getAll()

        if (!models.isNullOrEmpty()){

            val values : ArrayList<Entry> = arrayListOf()
            val dates : ArrayList<String> = arrayListOf()
            var topValue = 0.0.toDouble()

            for (i in models.indices){


                if (invalidate){

                    val modelDate = SimpleDateFormat("dd.MM.yyyy").parse(models[i].date)
                    val cDate = SimpleDateFormat("dd.MM.yyyy").parse(getCurrentDate())


                    if (abs(daysBetween(modelDate, cDate)) <= range){

                        values.add(
                            Entry(
                                i.toFloat(), (models[i].distance).toInt().toFloat()
                            )
                        )

                        topValue+=models[i].distance

                        dates.add(models[i].date)
                    }

                } else {
                    values.add(
                        Entry(
                            i.toFloat(), (models[i].distance).toInt().toFloat()
                        )
                    )
                    topValue+=models[i].distance
                    dates.add(models[i].date)
                }
            }

            textViewAll.text = roundDouble(topValue).toString()

            val chart = bottomView.findViewById<LineChart>(R.id.distance_chart)

            chart.invalidate()

            chart.description.isEnabled = false

            chart.legend.isEnabled = false
            chart.legend.textColor = Color.parseColor("#2a2d43")
            chart.legend.textSize = 14f

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(dates)
            chart.xAxis.textColor = Color.WHITE
            chart.xAxis.setDrawGridLines(false)
            chart.xAxis.granularity = 1f


            //chart.axisLeft.textColor = Color.WHITE
            chart.axisLeft.isEnabled = true
            chart.axisLeft.setDrawGridLines(true)
            chart.axisLeft.gridColor = Color.parseColor("#757575")
            chart.axisLeft.textColor = Color.parseColor("#757575")

            chart.axisRight.isEnabled = false
            chart.axisRight.textColor = Color.WHITE


            val lineDataSet = LineDataSet(values, "???????????????????? ?????????????????????? ?????????????? ?? ????????")

            lineDataSet.valueTextColor = Color.parseColor("#2a2d43")
            lineDataSet.valueTextSize = 14f
            lineDataSet.disableDashedLine()
            lineDataSet.lineWidth = 3f
            lineDataSet.setDrawFilled(true)
            lineDataSet.fillColor = Color.parseColor("#B15DFF")
            lineDataSet.setDrawHorizontalHighlightIndicator(false)
            lineDataSet.setDrawValues(false)

            val dataSets : ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(lineDataSet)

            chart.data = LineData(dataSets)

            activity.runOnUiThread {
                chart.animateY(500)
                chart.animateX(200)
            }

        }  else {
            bottomView.findViewById<LineChart>(R.id.distance_chart).visibility = View.GONE
        }
    }

    @SuppressLint("CutPasteId")
    fun showExpenses(
        database: AppDatabase, rootView: View, activity: Activity,
        invalidate: Boolean, range: Int = 0
    ){

        val mds = database.petrolDao().getAll()

        val resModels = arrayListOf<PetrolModel>()

        var rubExpenses = 0.0
        var grivnExpenses = 0.0
        var dollarsExpenses = 0.0
        var euroExpenses = 0.0
        var poundsExpenses = 0.0

        if (!mds.isNullOrEmpty()){
            for (i in mds.indices){

                if (invalidate){

                    val modelDate = SimpleDateFormat("dd.MM.yyyy").parse(mds[i].date)
                    val cDate = SimpleDateFormat("dd.MM.yyyy").parse(getCurrentDate())


                    if (abs(daysBetween(modelDate, cDate)) <= range){

                        resModels.add(mds[i])

                    }

                } else {

                    resModels.add(mds[i])

                }
            }

            for (m in resModels){
                when (m.currency) {
                    StaticVars.currencyValues[0] -> rubExpenses += m.amount * m.price
                    StaticVars.currencyValues[1] -> grivnExpenses += m.amount * m.price
                    StaticVars.currencyValues[2] -> dollarsExpenses += m.amount * m.price
                    StaticVars.currencyValues[3] -> euroExpenses += m.amount * m.price
                    StaticVars.currencyValues[4] -> poundsExpenses += m.amount * m.price
                }
            }
        }


        activity.runOnUiThread {
            if (rubExpenses > 0.0){
                rootView.findViewById<TextView>(R.id.rub_expenses).visibility = View.VISIBLE
                rootView.findViewById<TextView>(R.id.in_rubs_text_view).visibility = View.VISIBLE
                rootView.findViewById<LinearLayout>(R.id.fuel_stats_layout).visibility = View.VISIBLE
                rootView.findViewById<TextView>(R.id.rub_expenses).text = roundDouble(rubExpenses)
            }
            else {
                rootView.findViewById<TextView>(R.id.rub_expenses).visibility = View.GONE
                rootView.findViewById<TextView>(R.id.in_rubs_text_view).visibility = View.GONE
            }

            if (grivnExpenses > 0.0){
                rootView.findViewById<TextView>(R.id.grivn_expenses).text = roundDouble(grivnExpenses)
                rootView.findViewById<TextView>(R.id.grivn_expenses).visibility = View.VISIBLE
                rootView.findViewById<TextView>(R.id.in_grivn_text_view).visibility = View.VISIBLE
                rootView.findViewById<LinearLayout>(R.id.fuel_stats_layout).visibility = View.VISIBLE

            }
            else {
                rootView.findViewById<TextView>(R.id.grivn_expenses).visibility = View.GONE
                rootView.findViewById<TextView>(R.id.in_grivn_text_view).visibility = View.GONE
            }

            if (dollarsExpenses > 0.0){
                rootView.findViewById<TextView>(R.id.dollars_expenses).visibility = View.VISIBLE
                rootView.findViewById<TextView>(R.id.in_dollars_text_view).visibility = View.VISIBLE
                rootView.findViewById<LinearLayout>(R.id.fuel_stats_layout).visibility = View.VISIBLE

                rootView.findViewById<TextView>(R.id.dollars_expenses).text = roundDouble(dollarsExpenses)
            }
            else {
                rootView.findViewById<TextView>(R.id.dollars_expenses).visibility = View.GONE
                rootView.findViewById<TextView>(R.id.in_dollars_text_view).visibility = View.GONE
            }

            if (euroExpenses > 0.0) {
                rootView.findViewById<TextView>(R.id.euro_expenses).text = roundDouble(euroExpenses)
                rootView.findViewById<TextView>(R.id.euro_expenses).visibility = View.VISIBLE
                rootView.findViewById<TextView>(R.id.in_euro_text_view).visibility = View.VISIBLE
                rootView.findViewById<LinearLayout>(R.id.fuel_stats_layout).visibility = View.VISIBLE

            }
            else {
                rootView.findViewById<TextView>(R.id.euro_expenses).visibility = View.GONE
                rootView.findViewById<TextView>(R.id.in_euro_text_view).visibility = View.GONE
            }

            if (poundsExpenses > 0.0){
                rootView.findViewById<TextView>(R.id.pounds_expenses).text = roundDouble(poundsExpenses)
                rootView.findViewById<TextView>(R.id.pounds_expenses).visibility = View.VISIBLE
                rootView.findViewById<TextView>(R.id.in_pounds_text_view).visibility = View.VISIBLE
                rootView.findViewById<LinearLayout>(R.id.fuel_stats_layout).visibility = View.VISIBLE
            }
            else {
                rootView.findViewById<TextView>(R.id.pounds_expenses).visibility = View.GONE
                rootView.findViewById<TextView>(R.id.in_pounds_text_view).visibility = View.GONE
            }

            if (rubExpenses == 0.0 && grivnExpenses == 0.0 && dollarsExpenses == 0.0 && euroExpenses == 0.0 && poundsExpenses == 0.0)
                rootView.findViewById<LinearLayout>(R.id.fuel_stats_layout).visibility = View.GONE
        }

    }

    fun daysBetween(d1: Date, d2: Date): Long {
        return ((d2.time - d1.time) / (1000 * 60 * 60 * 24))
    }

}