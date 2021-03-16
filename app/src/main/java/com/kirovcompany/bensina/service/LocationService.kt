package com.kirovcompany.bensina.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.kirovcompany.bensina.MyLocationListener
import com.kirovcompany.bensina.StaticVars
import com.kirovcompany.bensina.interfaces.FragmentInit
import com.kirovcompany.bensina.localdb.AppDatabase
import com.kirovcompany.bensina.localdb.routeprogress.RouteProgressModel
import java.lang.Exception

@Suppress("DEPRECATION")
class LocationService : Service(), FragmentInit {

    private val handler = Handler()
    private lateinit var database : AppDatabase
    private var running = false
    private val staticVars = StaticVars()
    private var prevLocation = MyLocationListener.imHere

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        database = getAppDatabase(applicationContext)
        running = database.serviceDao().get().status!!
        try {
            setLocationListener()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun setLocationListener() {

        MyLocationListener.SetUpLocationListener(applicationContext)

        handler.post(object : Runnable {
            override fun run() {
                if (running) {
                    //сохранение скорости пользователя
                    try {
                        Toast.makeText(applicationContext, MyLocationListener.imHere?.speed.toString(), Toast.LENGTH_SHORT).show()
                        val routeProgressModel : RouteProgressModel?

                        val distance = MyLocationListener.imHere?.let { prevLocation?.let { it1 ->
                            calcDistance(it,
                                it1
                            ).toString()
                        } }

                        val speed = MyLocationListener.imHere?.speed.toString()

                        //todo и добавить подсчёт расхода топлива
                        val carRate = database.carModelDao().getLast().carRate?.let {
                            calcCarRate(speed.toDouble(), it.toDouble())
                        }

                        database
                            .routeProgressDao()
                            .insert(
                                RouteProgressModel(
                                    null,
                                    speed,
                                    distance,
                                    carRate.toString()
                                )
                            )
                        prevLocation = MyLocationListener.imHere
                    } catch (e : Exception){
                        e.printStackTrace()
                        Log.e("error", "error in saving RouteProgressModel in database")
                    }
                    handler.postDelayed(this, staticVars.locationDelay)
                }
            }
        })

    }

    private fun calcCarRate(speed : Double, rate : Double): Any {
        var b = (speed*rate)/100
        b += getAdditionalRate(b)
        return b
    }

    private fun getAdditionalRate(tempRate : Double) : Double{
        return if (tempRate > 0 && tempRate <= 40)
            3.0
        else if (tempRate > 40 && tempRate <= 70)
            2.0
        else if (tempRate > 70 && tempRate <= 80)
            0.0
        else if (tempRate > 80 && tempRate <= 120)
            -2.0
        else +1.0
    }

    private fun calcDistance(currLocation : Location, prevLocation : Location): Float {
        return prevLocation.distanceTo(currLocation)
    }



    override fun initViews() {

    }

}