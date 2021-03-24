package com.kirovcompany.benzina.service

import android.annotation.SuppressLint
import android.content.Context;
import android.content.SharedPreferences
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log
import com.kirovcompany.benzina.StaticVars


class MyLocationListener : LocationListener {

    override fun onLocationChanged(loc: Location) {
        imHere = loc

        latitude = loc.latitude
        longitude = loc.longitude

        //получение скорости движения
        speed = (loc.speed *3600/1000).toDouble()

        //подсчёт дистанции от предыдущей точки
        distance += calcDistance(imHere, prevLocation)
        //подсчёт расхода топлива
        rate += calcCarRate(distance.toDouble(), constCarRate, speed)
        prevLocation = loc



        Log.e(
            "results", "speed ${MyLocationListener.speed} " +
                    "rate ${MyLocationListener.rate} " +
                    "distance ${MyLocationListener.distance} " +
                    "coordinates ${MyLocationListener.longitude} ${MyLocationListener.latitude}"
        )

    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    companion object {
        // здесь будет всегда доступна самая последняя информация о местоположении пользователя.
        var preferences : SharedPreferences? = null

        var prevLocation : Location? = null
        var imHere : Location? = null
        var latitude : Double? = null
        var longitude : Double? = null
        var speed : Double = 0.0
        var rate : Double = 0.0
        var distance : Float = 0f
        var constCarRate : Double = 0.0

        // это нужно запустить в самом начале работы программы
        @SuppressLint("MissingPermission")
        fun setUpLocationListener(context: Context, carRate : Double){
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationListener: LocationListener = MyLocationListener()

            constCarRate = carRate


            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, 10f,
                locationListener
            ) // здесь можно указать другие более подходящие вам параметры
            imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
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