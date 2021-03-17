package com.kirovcompany.bensina.interfaces

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.kirovcompany.bensina.R
import com.kirovcompany.bensina.StaticVars
import com.kirovcompany.bensina.localdb.AppDatabase

interface ChartsUtil : FragmentUtil {

    fun showAverageSpeed(database : AppDatabase, bottomView : View){
        val models = database.routesPerDayModel().getAll()
        if (!models.isNullOrEmpty()){

            val values : ArrayList<BarEntry> = arrayListOf()
            val dates : ArrayList<String> = arrayListOf()

            for (i in models.indices){
                values.add(
                    BarEntry(
                    i.toFloat(), (models[i].averageSpeed).toInt().toFloat()
                )
                )
                dates.add(models[i].date)
            }

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


            //chart.axisLeft.textColor = Color.WHITE
            chart.axisLeft.isEnabled = true
            chart.axisLeft.setDrawGridLines(false)

            chart.axisRight.isEnabled = false
            chart.axisRight.textColor = Color.WHITE


            val barDataSet = BarDataSet(values, "Количество выполненных поездок в день")

            barDataSet.valueTextColor = Color.parseColor("#2a2d43")
            barDataSet.valueTextSize = 14f
            /*
            barDataSet.disableDashedLine()
            barDataSet.lineWidth = 3f
            barDataSet.setDrawFilled(true)
            barDataSet.fillColor = Color.parseColor("#B15DFF")
            barDataSet.setDrawHorizontalHighlightIndicator(false)

             */

            val data = BarData(barDataSet)
            chart.data = data
            chart.invalidate()

            //chart.animateY(500)
        } else {
            bottomView.findViewById<BarChart>(R.id.speed_chart).visibility = View.GONE
        }
    }

    fun showRateGraphic(database : AppDatabase, bottomView : View){
        val models = database.routesPerDayModel().getAll()
        if (!models.isNullOrEmpty()){

            val values : ArrayList<BarEntry> = arrayListOf()
            val dates : ArrayList<String> = arrayListOf()

            for (i in models.indices){
                values.add(BarEntry(
                    i.toFloat(), (models[i].averageCarRate).toInt().toFloat()
                ))
                dates.add(models[i].date)
            }

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


            //chart.axisLeft.textColor = Color.WHITE
            chart.axisLeft.isEnabled = true
            chart.axisLeft.setDrawGridLines(false)

            chart.axisRight.isEnabled = false
            chart.axisRight.textColor = Color.WHITE


            val barDataSet = BarDataSet(values, "Количество выполненных поездок в день")

            barDataSet.valueTextColor = Color.parseColor("#2a2d43")
            barDataSet.valueTextSize = 14f
            /*
            barDataSet.disableDashedLine()
            barDataSet.lineWidth = 3f
            barDataSet.setDrawFilled(true)
            barDataSet.fillColor = Color.parseColor("#B15DFF")
            barDataSet.setDrawHorizontalHighlightIndicator(false)

             */

            val data = BarData(barDataSet)
            chart.data = data
            chart.invalidate()

            //chart.animateY(500)
        }  else {
            bottomView.findViewById<BarChart>(R.id.rate_chart).visibility = View.GONE
        }
    }

    fun showRoutesPerDayGraphic(database : AppDatabase, bottomView : View) {
        val models = database.routesPerDayModel().getAll()
        if (!models.isNullOrEmpty()){

            val values : ArrayList<Entry> = arrayListOf()
            val dates : ArrayList<String> = arrayListOf()

            for (i in models.indices){
                values.add(
                    Entry(
                    i.toFloat(), (models[i].num).toInt().toFloat()
                )
                )
                dates.add(models[i].date)
            }

            val chart = bottomView.findViewById<LineChart>(R.id.routes_chart)
            chart.invalidate()

            chart.setPinchZoom(false)
            chart.isDoubleTapToZoomEnabled = false
            chart.description.isEnabled = false

            chart.legend.isEnabled = false
            chart.legend.textColor = Color.parseColor("#2a2d43")
            chart.legend.textSize = 14f

            //chart.xAxis.textColor = Color.WHITE
            chart.xAxis.setDrawGridLines(false)
            chart.xAxis.valueFormatter = DayAxisValueFormatter(chart, dates)


            //chart.axisLeft.textColor = Color.WHITE
            chart.axisLeft.isEnabled = true
            chart.axisLeft.setDrawGridLines(false)

            chart.axisRight.isEnabled = false
            chart.axisRight.textColor = Color.WHITE


            val lineDataSet = LineDataSet(values, "Количество выполненных поездок в день")

            lineDataSet.valueTextColor = Color.parseColor("#2a2d43")
            lineDataSet.valueTextSize = 14f
            lineDataSet.disableDashedLine()
            lineDataSet.lineWidth = 3f
            lineDataSet.setDrawFilled(true)
            lineDataSet.fillColor = Color.parseColor("#B15DFF")
            lineDataSet.setDrawHorizontalHighlightIndicator(false)

            val dataSets : ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(lineDataSet)

            chart.data = LineData(dataSets)
        }  else {
            bottomView.findViewById<LineChart>(R.id.routes_chart).visibility = View.GONE
        }
    }

    fun showExpenses(database: AppDatabase, rootView : View){
        val staticVars = StaticVars()
        val mds = database.petrolDao().getAll()
        var rubExpenses = 0.0
        var grivnExpenses = 0.0
        var dollarsExpenses = 0.0
        var euroExpenses = 0.0
        var poundsExpenses = 0.0

        for (m in mds){
            when (m.currency) {
                staticVars.currencyValues[0] -> rubExpenses += m.amount*m.price
                staticVars.currencyValues[1] -> grivnExpenses += m.amount*m.price
                staticVars.currencyValues[2] -> dollarsExpenses += m.amount*m.price
                staticVars.currencyValues[3] -> euroExpenses += m.amount*m.price
                staticVars.currencyValues[4] -> poundsExpenses += m.amount*m.price
            }
        }

        if (rubExpenses > 0.0) rootView.findViewById<TextView>(R.id.rub_expenses).text = rubExpenses.toInt().toString()
        else rootView.findViewById<TextView>(R.id.rub_expenses).visibility = View.GONE

        if (grivnExpenses > 0.0) rootView.findViewById<TextView>(R.id.grivn_expenses).text = grivnExpenses.toInt().toString()
        else rootView.findViewById<TextView>(R.id.grivn_expenses).visibility = View.GONE

        if (dollarsExpenses > 0.0) rootView.findViewById<TextView>(R.id.dollars_expenses).text = dollarsExpenses.toInt().toString()
        else rootView.findViewById<TextView>(R.id.dollars_expenses).visibility = View.GONE

        if (euroExpenses > 0.0) rootView.findViewById<TextView>(R.id.euro_expenses).text = euroExpenses.toInt().toString()
        else rootView.findViewById<TextView>(R.id.euro_expenses).visibility = View.GONE

        if (poundsExpenses > 0.0) rootView.findViewById<TextView>(R.id.pounds_expenses).text = poundsExpenses.toInt().toString()
        else rootView.findViewById<TextView>(R.id.pounds_expenses).visibility = View.GONE

    }

    class DayAxisValueFormatter(private val chart: LineChart,
                                private val dates : ArrayList<String>) : ValueFormatter() {


        override fun getFormattedValue(value: Float): String {
            return dates[value.toInt()].toString()
        }
    }

}