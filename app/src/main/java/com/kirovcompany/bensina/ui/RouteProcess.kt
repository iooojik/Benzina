package com.kirovcompany.bensina.ui

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.kirovcompany.bensina.R
import com.kirovcompany.bensina.StaticVars
import com.kirovcompany.bensina.interfaces.ChartsUtil
import com.kirovcompany.bensina.interfaces.FragmentUtil
import com.kirovcompany.bensina.localdb.AppDatabase
import com.kirovcompany.bensina.localdb.routesperday.RoutesPerDayModel
import com.kirovcompany.bensina.localdb.service.ServiceModel
import com.kirovcompany.bensina.service.LocationService


@Suppress("SENSELESS_COMPARISON", "DEPRECATION")
class RouteProcess : Fragment(), FragmentUtil, View.OnClickListener, ChartsUtil {

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
    private lateinit var btn : View

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_route_process, container, false)
        //инициализация всех вьюшек
        initViews()
        prepareViews()
        //блокируем переход назад
        blockGoBack(requireActivity(), this)
        checkPermissions()
        return rootView
    }

    private fun prepareViews() {
        getStatus()
        val inflater = layoutInflater
        btn = if (!running){
            inflater.inflate(R.layout.button_start, null)
        } else inflater.inflate(R.layout.button_stop, null)
        btn.setOnClickListener(this)
        rootView.findViewById<LinearLayout>(R.id.layout_stats).addView(btn)

        synchronized(this){
            showAverageSpeed(database, rootView)
            showRateGraphic(database, rootView)
            showRoutesPerDayGraphic(database, rootView)
            showExpenses(database, rootView)
        }
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
        mService = Intent(requireContext(), LocationService::class.java)
        requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.fab).show()
        setFabAction(requireActivity().findViewById(R.id.fab), requireContext(), requireActivity())
    }

    private fun getStatus(){
        running = database.serviceDao().get().status
    }

    private fun startLocationService(){
        val t = database.timerDao().get()
        t.seconds = 0
        database.timerDao().update(t)

        running = true
        val statusModel = database.serviceDao().get()
        statusModel.status = running
        database.serviceDao().update(statusModel)

        requireActivity().stopService(mService)
        requireActivity().startService(mService)

        updateUI()
    }

    private fun updateUI(){

        timer.post(object : Runnable{
            override fun run() {
                if (running){
                    requireActivity().runOnUiThread { showTime() }
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
        carDistanceTextView.text = roundDouble(distance)
    }

    private fun showCarRate() {
        carRateTextView.text = roundDouble(calcCarRate())
    }

    private fun showSpeed(){
        val routeModel = database.routeProgressDao().getLast()
        speedTextView.text = roundDouble(routeModel.speed.toDouble())
    }

    private fun calcCarRate() : Double{
        val mds = database.routeProgressDao().getAll()
        var rate = 0.0
        for (m in mds){
            rate += m.carRate.toDouble()
        }
        rate /= mds.size
        if (rate.isNaN())
            rate = 0.0
        return rate
    }

    private fun calcCarSpeed() : Double{
        val mds = database.routeProgressDao().getAll()
        var speed = 0.0
        for (m in mds){
            speed += m.speed.toDouble()
        }
        speed /= mds.size
        if (speed.isNaN())
            speed = 0.0
        return speed
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

                    var routeCounter = database.routesPerDayModel().getByDate(getCurrentDate())

                    if (routeCounter == null){
                        routeCounter = RoutesPerDayModel(null, getCurrentDate(), 1, calcCarRate(), calcCarSpeed())
                        database.routesPerDayModel().insert(routeCounter)
                    } else {
                        routeCounter.num = routeCounter.num + 1

                        if (calcCarRate() != 0.0)
                            routeCounter.averageCarRate = (routeCounter.averageCarRate + calcCarRate()) / 2.0

                        if (calcCarSpeed() != 0.0)
                            routeCounter.averageSpeed = (routeCounter.averageSpeed + calcCarSpeed()) / 2.0

                        database.routesPerDayModel().update(routeCounter)
                    }

                   requireActivity().findNavController(R.id.nav_host_fragment)
                           .navigate(R.id.navigation_routeProcess)
                }
                rootView.findViewById<LinearLayout>(R.id.layout_stats).removeView(btn)
                prepareViews()
            }

            R.id.start_button -> {
                if (checkPermissions()){
                    //запускаем сервис
                    startLocationService()

                    rootView.findViewById<LinearLayout>(R.id.layout_stats).removeView(btn)
                    prepareViews()
                    //получаем пройденную дистанцию
                    calcDistance()

                }
            }

            R.id.fab -> {
                val bottomSheetDialog = BottomSheetPetrol(requireContext(), requireActivity()).bottomSheetDialog
                bottomSheetDialog.show()
            }
        }
    }

    private fun checkPermissions() : Boolean {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(),
                listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).toTypedArray(),
                101
            )
        } else return true
        return false
    }

}