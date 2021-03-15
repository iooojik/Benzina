package com.kirovcompany.bensina.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.kirovcompany.bensina.MyLocationListener
import com.kirovcompany.bensina.interfaces.FragmentInit
import com.kirovcompany.bensina.localdb.AppDatabase
import com.kirovcompany.bensina.localdb.routeprogress.RouteProgressModel
import java.lang.Exception

@Suppress("DEPRECATION")
class LocationService : Service(), FragmentInit {

    private val handler = Handler()
    private lateinit var database : AppDatabase
    private var running = false

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
                        database.routeProgressDao().insert(RouteProgressModel(null, MyLocationListener.imHere?.speed.toString()))
                    } catch (e : Exception){
                        e.printStackTrace()
                        Log.e("error", "error in saving RouteProgressModel in database")
                    }
                    handler.postDelayed(this, 3000)
                }
            }
        })

    }

    override fun initViews() {

    }

}