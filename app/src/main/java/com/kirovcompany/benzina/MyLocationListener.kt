package com.kirovcompany.benzina

import android.annotation.SuppressLint
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


class MyLocationListener : LocationListener {

    override fun onLocationChanged(loc: Location) {
        imHere = loc
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    companion object {
        // здесь будет всегда доступна самая последняя информация о местоположении пользователя.
        var imHere : Location? = null

        // это нужно запустить в самом начале работы программы
        @SuppressLint("MissingPermission")
        fun setUpLocationListener(context: Context){
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationListener: LocationListener = MyLocationListener()

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, 10f,
                locationListener
            ) // здесь можно указать другие более подходящие вам параметры
            imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
    }
}