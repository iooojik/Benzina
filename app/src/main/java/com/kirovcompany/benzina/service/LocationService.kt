package com.kirovcompany.benzina.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.kirovcompany.benzina.StaticVars
import com.kirovcompany.benzina.interfaces.ServiceUtil
import com.kirovcompany.benzina.localdb.AppDatabase
import java.lang.Exception


class LocationService : Service(), ServiceUtil {

    private val handler = Handler()
    private val timer = Handler()
    private lateinit var database : AppDatabase
    private var running = false
    private lateinit var preferences : SharedPreferences

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        database = getAppDatabase(applicationContext)
        preferences = getSharedPreferences(StaticVars.preferencesName, Context.MODE_PRIVATE)
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
        MyLocationListener.rate = preferences.getFloat(StaticVars.preferencesRate, 0f).toDouble()
        MyLocationListener.distance = preferences.getFloat(StaticVars.preferencesDistance, 0f)

        handler.post(object : Runnable {
            override fun run() {
                running = database.serviceDao().get().status

                if (running) {
                    //сохранение скорости пользователя
                    preferences.edit().putFloat(StaticVars.preferencesSpeed, MyLocationListener.speed.toFloat()).apply()
                    preferences.edit().putFloat(StaticVars.preferencesDistance, MyLocationListener.distance).apply()
                    preferences.edit().putFloat(StaticVars.preferencesRate, MyLocationListener.rate.toFloat()).apply()

                    handler.postDelayed(this, StaticVars.locationDelay)
                }
            }
        })

    }

    private fun setTimer(){
        //таймер с записью в бд
        Handler().post(object : Runnable {
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