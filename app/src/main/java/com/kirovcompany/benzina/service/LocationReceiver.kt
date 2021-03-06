package com.kirovcompany.benzina.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val intentService = Intent(context, LocationService::class.java)
        context!!.startService(intentService)
    }
}