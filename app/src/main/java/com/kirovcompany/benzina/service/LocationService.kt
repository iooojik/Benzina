package com.kirovcompany.benzina.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.kirovcompany.benzina.MyLocationListener
import com.kirovcompany.benzina.StaticVars
import com.kirovcompany.benzina.interfaces.ServiceUtil
import com.kirovcompany.benzina.localdb.AppDatabase
import com.kirovcompany.benzina.localdb.routeprogress.RouteProgressModel
import java.lang.Exception

@Suppress("DEPRECATION")
class LocationService : Service(), ServiceUtil {

    private val handler = Handler()
    private val timer = Handler()
    private lateinit var database : AppDatabase
    private var running = false
    private var tempRate = 0.0
    private var tempDistance = 0.0

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        database = getAppDatabase(applicationContext)

        if (!database.routeProgressDao().getAll().isNullOrEmpty()) {
            tempDistance = database.routeProgressDao().getLast().distance.toDouble()
            tempRate = database.routeProgressDao().getLast().carRate.toDouble()
        }

        running = database.serviceDao().get().status

        try {
            setTimer()
            setLocationListener()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun setLocationListener() {

        MyLocationListener.setUpLocationListener(applicationContext, database.carModelDao().getLast().carRate.toDouble())

        handler.post(object : Runnable {
            override fun run() {
                running = database.serviceDao().get().status

                if (running) {
                    //сохранение скорости пользователя
                    try {

                        //подсчёт дистанции от предыдущей точки
                        tempDistance += MyLocationListener.distance

                        //получение скорости движения
                        val speed = MyLocationListener.speed

                        //подсчёт расхода топлива
                        tempRate += MyLocationListener.rate

                        Log.e(
                            "results", "speed $speed " +
                                "rate $tempRate " +
                                "distance $tempDistance " +
                                "coordinates ${MyLocationListener.longitude} ${MyLocationListener.latitude}"
                        )

                        database
                            .routeProgressDao()
                            .insert(
                                RouteProgressModel(
                                    null,
                                    speed.toString(),
                                    tempDistance.toString(),
                                    tempRate.toString()
                                )
                            )

                    } catch (e : Exception){

                        e.printStackTrace()
                        Log.e("error", "error in saving RouteProgressModel in database")

                    }

                    handler.postDelayed(this, StaticVars.locationDelay)
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



}