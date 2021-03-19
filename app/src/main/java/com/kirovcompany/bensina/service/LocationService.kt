package com.kirovcompany.bensina.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.kirovcompany.bensina.MyLocationListener
import com.kirovcompany.bensina.StaticVars
import com.kirovcompany.bensina.interfaces.ServiceUtil
import com.kirovcompany.bensina.localdb.AppDatabase
import com.kirovcompany.bensina.localdb.routeprogress.RouteProgressModel
import java.lang.Exception

@Suppress("DEPRECATION")
class LocationService : Service(), ServiceUtil {

    private val handler = Handler()
    private val timer = Handler()
    private lateinit var database : AppDatabase
    private var running = false
    private val staticVars = StaticVars()
    private var prevLocation = MyLocationListener.imHere
    private var tempRate = 0.0.toDouble()
    private var tempDistance = 0.0.toDouble()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        MyLocationListener.setUpLocationListener(applicationContext)
        database = getAppDatabase(applicationContext)

        if (!database.routeProgressDao().getAll().isNullOrEmpty()) {
            tempDistance = database.routeProgressDao().getLast().distance.toDouble()
            tempRate = database.routeProgressDao().getLast().carRate.toDouble()
        }

        prevLocation = MyLocationListener.imHere
        running = database.serviceDao().get().status

        try {
            setTimer()
            setLocationListener()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun setLocationListener() {

        MyLocationListener.setUpLocationListener(applicationContext)

        handler.post(object : Runnable {
            override fun run() {
                running = database.serviceDao().get().status!!

                if (running) {
                    //сохранение скорости пользователя
                    try {

                        //подсчёт дистанции от предыдущей точки
                        val distance = calcDistance(MyLocationListener.imHere, prevLocation)
                        tempDistance += distance

                        //получение скорости движения
                        val speed = (MyLocationListener.imHere?.speed!! *3600/1000).toString()

                        //подсчёт расхода топлива
                        val carRate = calcCarRate(
                                distance.toDouble(),
                                database.carModelDao().getLast().carRate.toDouble(),
                                speed.toDouble()
                        )

                        tempRate += carRate

                        Log.e("ttt", "speed $speed rate $tempRate distance $tempDistance " +
                                "coordinates ${MyLocationListener.imHere?.longitude} ${MyLocationListener.imHere?.latitude}")

                        database
                            .routeProgressDao()
                            .insert(
                                RouteProgressModel(
                                    null,
                                    speed,
                                    tempDistance.toString(),
                                    tempRate.toString()
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

    private fun setTimer(){
        //таймер с записью в бд
        timer.post(object : Runnable {
            override fun run() {
                if (running){
                    val timerModel = database.timerDao().get()
                    timerModel.seconds = timerModel.seconds?.plus(1)
                    database.timerDao().update(timerModel)
                    handler.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun calcCarRate(distance : Double, rate : Double, speed: Double): Double {
        //подсчёт расхода топлива
        return (distance*(rate + getAdditionalRate(speed)))/100
    }

    private fun getAdditionalRate(speed : Double) : Double{
        return if (speed > 0 && speed <= 40)
            +3.0
        else if (speed > 40 && speed <= 70)
            +2.0
        else if (speed > 70 && speed <= 80)
            0.0
        else if (speed > 80 && speed <= 120)
            -2.0
        else +1.0
    }

    private fun calcDistance(currLocation : Location?, prevLocation : Location?): Float {
        //подсчёт пройденной дистанции в км
        return if (prevLocation != null && currLocation != null)
            prevLocation.distanceTo(currLocation)/1000
        else 0f
    }

}