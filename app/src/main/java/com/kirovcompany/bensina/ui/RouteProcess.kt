package com.kirovcompany.bensina.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.kirovcompany.bensina.R
import com.kirovcompany.bensina.StaticVars
import com.kirovcompany.bensina.interfaces.FragmentUtil
import com.kirovcompany.bensina.localdb.AppDatabase
import com.kirovcompany.bensina.localdb.service.ServiceModel
import com.kirovcompany.bensina.service.LocationService
import kotlin.concurrent.thread


@Suppress("SENSELESS_COMPARISON", "DEPRECATION")
class RouteProcess : Fragment(), FragmentUtil, View.OnClickListener {

    private lateinit var rootView : View
    private val staticVars = StaticVars()
    private lateinit var preferences: SharedPreferences
    private lateinit var database: AppDatabase
    private val handler = Handler()
    private val timer = Handler()
    private lateinit var mService : Intent
    private var distance = 0.toDouble()
    private lateinit var speedTextView : TextView
    private lateinit var carRateTextView : TextView
    private lateinit var carDistanceTextView : TextView
    private lateinit var timerTextView : TextView
    private var running = true

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_route_process, container, false)
        //инициализация всех вьюшек
        initViews()
        //блокируем переход назад
        blockGoBack(requireActivity(), this)
        //получаем пройденную дистанцию
        calcDistance()
        //запускаем сервис
        startLocationService()
        return rootView
    }

    private fun calcDistance() {
        val mds = database.routeProgressDao().getAll()
        if (!mds.isNullOrEmpty()){
            for (m in mds){
                try {
                    distance += m.distance.toDouble()
                } catch (e : java.lang.Exception){
                    e.printStackTrace()
                }

            }
        }
    }

    override fun initViews() {
        database = getAppDatabase(requireContext())
        preferences = getSharedPreferences(requireActivity())
        speedTextView = rootView.findViewById(R.id.speed_text_view)
        carRateTextView = rootView.findViewById(R.id.rate_text_view)
        carDistanceTextView = rootView.findViewById(R.id.distance_text_view)
        timerTextView = rootView.findViewById(R.id.timer_text_view)
        rootView.findViewById<Button>(R.id.stop_button).setOnClickListener(this)
        mService = Intent(requireContext(), LocationService::class.java)
        requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.fab).show()
        setFabAction(requireActivity().findViewById(R.id.fab), requireContext(), requireActivity())

        val m = database.timerDao().get()
        m.seconds = 0
        database.timerDao().update(m)
    }

    private fun startLocationService(){
        if (database.serviceDao().get() == null){
            database.serviceDao().insert(ServiceModel(null, running))
        } else {
            val m = database.serviceDao().get()
            m.status = running
            database.serviceDao().update(m)
        }
        requireActivity().stopService(mService)
        requireActivity().startService(mService)

        updateUI()
    }

    private fun updateUI(){

        timer.post(object : Runnable{
            override fun run() {
                if (running){
                    showTime()
                    timer.postDelayed(this, 1000)
                }
            }

        })

        handler.post(object : Runnable {
            override fun run() {
                if (running){
                    try {
                        requireActivity().runOnUiThread {
                            //показываем скорость
                            showSpeed()
                            showCarRate()
                            showDistance()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("update ui", "error in updating ui. check database")
                    }

                    handler.postDelayed(this, staticVars.locationDelay + 100)
                }
            }

        })
    }

    private fun showTime() {
        val timer = database.timerDao().get()
        if (timer != null){
            timerTextView.text = timer.seconds?.let { timeToString(it.toLong()) }
        }
    }

    private fun timeToString(secs: Long): String {
        val hour = secs / 3600
        val min = secs / 60 % 60
        val sec = secs / 1 % 60
        return String.format("%02d:%02d:%02d", hour, min, sec)
    }

    private fun showDistance() {
        val routeModel = database.routeProgressDao().getLast()
        distance += routeModel.distance.toDouble()
        carDistanceTextView.text = distance.toString()
    }

    private fun showCarRate() {
        val routeModel = database.routeProgressDao().getLast()
        carRateTextView.text = routeModel.carRate.toString()
    }

    private fun showSpeed(){
        val routeModel = database.routeProgressDao().getLast()
        speedTextView.text = routeModel.speed.toString()
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.stop_button -> {

                running = false
                val md = database.serviceDao().get()

                if (md != null){
                    requireActivity().stopService(mService)
                    md.status = running
                    database.serviceDao().update(md)

                   requireActivity().findNavController(R.id.nav_host_fragment)
                           .navigate(R.id.navigation_beginRoute)
                }

            }
        }
    }

}