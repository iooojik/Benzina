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
import com.kirovcompany.bensina.interfaces.FragmentUtil
import com.kirovcompany.bensina.interfaces.ServiceUtil
import com.kirovcompany.bensina.localdb.AppDatabase
import com.kirovcompany.bensina.localdb.routeprogress.RouteProgressModel
import java.lang.Exception
import kotlin.concurrent.thread

@Suppress("DEPRECATION")
class LocationService : Service(), ServiceUtil {

    private val handler = Handler()
    private val timer = Handler()
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
                        Toast.makeText(applicationContext, MyLocationListener.imHere?.speed.toString(), Toast.LENGTH_SHORT).show()

                        //подсчёт дистанции от предыдущей точки
                        var distance = MyLocationListener.imHere?.let { prevLocation?.let { it1 ->
                            calcDistance(it,
                                it1
                            )
                        } }
                        //если она null, то дистанция = 0
                        if (distance == null) distance = 0f

                        //получение скорости движения
                        val speed = MyLocationListener.imHere?.speed.toString()

                        //подсчёт расхода топлива
                        val carRate = database.carModelDao().getLast().carRate?.let {
                            (calcCarRate(speed.toDouble(), it.toDouble()) * distance) / 100f
                        }

                        database
                            .routeProgressDao()
                            .insert(
                                RouteProgressModel(
                                    null,
                                    speed,
                                    distance.toString(),
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

    private fun calcCarRate(speed : Double, rate : Double): Double {
        //подсчёт расхода топлива
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
        //подсчёт пройденной дистанции
        return prevLocation.distanceTo(currLocation)
    }

}