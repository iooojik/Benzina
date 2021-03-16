package com.kirovcompany.bensina.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kirovcompany.bensina.R
import com.kirovcompany.bensina.StaticVars
import com.kirovcompany.bensina.interfaces.FragmentInit
import com.kirovcompany.bensina.localdb.AppDatabase
import com.kirovcompany.bensina.localdb.service.ServiceModel
import com.kirovcompany.bensina.service.LocationService
import org.w3c.dom.Text


@Suppress("SENSELESS_COMPARISON", "DEPRECATION")
class RouteProcess : Fragment(), FragmentInit {

    private lateinit var rootView : View
    private val staticVars = StaticVars()
    private lateinit var preferences: SharedPreferences
    private lateinit var database: AppDatabase
    private val handler = Handler()
    private lateinit var speedTextView : TextView
    private lateinit var carRateTextView : TextView
    private lateinit var carDistanceTextView : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_route_process, container, false)
        initViews()
        startLocationService()
        return rootView
    }

    override fun initViews() {
        database = getAppDatabase(requireContext())
        preferences = getSharedPreferences(requireActivity())
        speedTextView = rootView.findViewById(R.id.speed_text_view)
        carRateTextView = rootView.findViewById(R.id.car_rate_field)
        carDistanceTextView = rootView.findViewById(R.id.distance_text_view)
    }

    private fun startLocationService(){
        if (database.serviceDao().get() == null){
            database.serviceDao().insert(ServiceModel(null, true))
        } else {
            val m = database.serviceDao().get()
            m.status = true
            database.serviceDao().update(m)
        }
        val locationService = Intent(requireContext(), LocationService::class.java)
        requireActivity().startService(locationService)
        updateUI()
    }

    private fun updateUI(){
        handler.post(object : Runnable {
            override fun run() {
                try {
                    requireActivity().runOnUiThread {
                        //показываем скорость
                        showSpeed()
                        showCarRate()
                        showDistance()
                    }
                } catch (e : Exception){
                    e.printStackTrace()
                    Log.e("update ui", "error in updating ui. check database")
                }

                handler.postDelayed(this, staticVars.locationDelay + 100)
            }

        })
    }

    private fun showDistance() {
        val routeModel = database.routeProgressDao().getLast()
        carDistanceTextView.text = routeModel.distance.toString()
    }

    private fun showCarRate() {
        val routeModel = database.routeProgressDao().getLast()
        carRateTextView.text = routeModel.carRate.toString()
    }

    private fun showSpeed(){
        val routeModel = database.routeProgressDao().getLast()
        speedTextView.text = routeModel.speed.toString()
    }

}