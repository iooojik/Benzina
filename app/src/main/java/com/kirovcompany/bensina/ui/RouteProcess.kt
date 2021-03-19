package com.kirovcompany.bensina.ui

import android.Manifest
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.kirovcompany.bensina.R
import com.kirovcompany.bensina.StaticVars
import com.kirovcompany.bensina.interfaces.ChartsUtil
import com.kirovcompany.bensina.interfaces.FragmentUtil
import com.kirovcompany.bensina.localdb.AppDatabase
import com.kirovcompany.bensina.localdb.routesperday.RoutesPerDayModel
import com.kirovcompany.bensina.service.LocationService
import kotlin.concurrent.thread


@Suppress("SENSELESS_COMPARISON", "DEPRECATION")
class RouteProcess : Fragment(), FragmentUtil, View.OnClickListener, ChartsUtil {

    private lateinit var rootView : View
    private val staticVars = StaticVars()
    private lateinit var preferences: SharedPreferences
    private lateinit var database: AppDatabase
    private val handler = Handler()
    private val timer = Handler()
    private lateinit var mService : Intent
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
        //инициализация всех вьюшек и переменных
        initViews()
        //изменение фрагмента в зависимости от переменной @running
        prepareViews()
        //блокируем переход назад
        blockGoBack(requireActivity(), this)

        checkPermissions()

        return rootView
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
        setFabAction(
            requireActivity().findViewById(R.id.fab),
            requireContext(),
            requireActivity(),
            this
        )
        getStatus()
    }

    private fun prepareViews() {
        //добавление кнопки в зависимости от переменной @running
        showButtonStartStop()

        //отображение статистики
        showStatistics(false)

        if (running){
            restartService(mService)
            updateUI()
        }

    }

    private fun showButtonStartStop(){
        //добавление кнопки в зависимости от переменной @running
        btn = if (!running){
            layoutInflater.inflate(R.layout.button_start, null)
        } else layoutInflater.inflate(R.layout.button_stop, null)
        //слушатель
        btn.setOnClickListener(this)
        //добавление на вьюшку
        rootView.findViewById<LinearLayout>(R.id.layout_stats).addView(btn)
    }

    private fun updateUI(){
        //таймер
        timer.post(object : Runnable {
            override fun run() {
                if (running) {
                    requireActivity().runOnUiThread { showTime() }
                    timer.postDelayed(this, 1000)
                }
            }

        })

        //отслеживание изменения данных

        handler.post(object : Runnable {
            override fun run() {
                if (running) {
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

    fun showStatistics(isUpdate: Boolean){
        //синхронно выполняются методы
        synchronized(this){
            //если была хотя бы одна запись, то показываем графики
            //иначе пропускаем метод

            if (!database.routesPerDayModel().getAll().isNullOrEmpty()){
                //если первая загрузка, то добавляем вьюшки
                if (!isUpdate)
                rootView.findViewById<LinearLayout>(R.id.statistics_layout).addView(
                    layoutInflater.inflate(R.layout.graphics_items, null)
                )

                try {
                    //показываем график скорости
                    showAverageSpeed(
                        database,
                        rootView,
                        requireActivity(),
                        false,
                        0,
                        rootView.findViewById(R.id.speed_count)
                    )
                    //показываем график расхода
                    showRateGraphic(
                        database,
                        rootView,
                        requireActivity(),
                        false,
                        0,
                        rootView.findViewById(R.id.rates_count)
                    )
                    //показываем график количества поездок в день
                    showRoutesPerDayGraphic(
                        database,
                        rootView,
                        requireActivity(),
                        false,
                        0,
                        rootView.findViewById(R.id.routes_count)
                    )
                    //показываем график пройденной дистанции
                    showDistance(
                        database,
                        rootView,
                        requireActivity(),
                        false,
                        0,
                        rootView.findViewById(R.id.distance_count)
                    )
                    //слушатели на кнпоки 7дней/30дней/180дней/365 дней
                    rootView.findViewById<Chip>(R.id.last_week_routes).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_month_routes).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_half_year_routes).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_year_routes).setOnClickListener(this)

                    rootView.findViewById<Chip>(R.id.last_week_rate).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_month_rate).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_half_year_rate).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_year_rate).setOnClickListener(this)

                    rootView.findViewById<Chip>(R.id.last_week_speed).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_month_speed).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_half_year_speed).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_year_speed).setOnClickListener(this)

                    rootView.findViewById<Chip>(R.id.last_week_distance).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_month_distance).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_half_year_distance).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_year_distance).setOnClickListener(this)

                    rootView.findViewById<Chip>(R.id.last_week_fuel).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_month_fuel).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_half_year_fuel).setOnClickListener(this)
                    rootView.findViewById<Chip>(R.id.last_year_fuel).setOnClickListener(this)

                } catch (e: java.lang.Exception){
                    e.printStackTrace()
                    Log.e("error", "old api")
                }
            }
                //отображение статистики
                showExpenses(
                    database,
                    rootView,
                    requireActivity(),
                    false,
                    0
                )

        }
    }

    private fun getStatus(){
        running = database.serviceDao().get().status
    }

    private fun startLocationService(){
        //обнуляем таймер
        val t = database.timerDao().get()
        t.seconds = 0
        database.timerDao().update(t)

        //изменяем состояние запуска
        running = true
        val statusModel = database.serviceDao().get()
        statusModel.status = running
        database.serviceDao().update(statusModel)

        //запуск сервиса
        restartService(mService)

        //запуск обновления ui
        updateUI()
    }

    private fun restartService(intent: Intent){
        thread {
            requireActivity().stopService(intent)
            Thread.sleep(6000)
            requireActivity().startService(intent)
        }

    }

    private fun isMyServiceRunning(): Boolean {
        val manager = requireActivity().getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {

            if (LocationService::class.simpleName.toString() == service.service.className) {
                return true
            }
        }
        return false
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
        carDistanceTextView.text = roundDouble(routeModel.distance.toDouble()).toString()
    }

    private fun showCarRate() {
        //отображение расхода
        carRateTextView.text = roundDouble(calcCarRate())
    }

    private fun showSpeed(){
        //отображение скорости
        //получение последней записи
        val routeModel = database.routeProgressDao().getLast()
        //отображение скорости
        speedTextView.text = roundDouble(routeModel.speed.toDouble())
    }

    private fun calcCarRate() : Double{
        //последняя запись - общий расход
        val mds = database.routeProgressDao().getLast()
        return mds.carRate.toDouble()
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
                //изменяем статус работы сервиса
                running = false
                //обновление данных
                val md = database.serviceDao().get()

                if (md != null) {
                    //останавливаем сервис
                    requireActivity().stopService(mService)

                    //изменяем статус работы сервиса
                    md.status = running
                    database.serviceDao().update(md)

                    //увеличиваем количество маршрутов в день
                    var routeCounter = database.routesPerDayModel().getByDate(getCurrentDate())

                    val distance = database.routeProgressDao().getLast().distance.toDouble()

                    if (routeCounter == null) {
                        routeCounter = RoutesPerDayModel(
                            null,
                            getCurrentDate(),
                            1,
                            calcCarRate(),
                            calcCarSpeed(),
                            distance
                        )
                        database.routesPerDayModel().insert(routeCounter)
                    } else {

                        routeCounter.num = routeCounter.num + 1

                        if (calcCarRate() != 0.0)
                            routeCounter.averageCarRate =
                                (routeCounter.averageCarRate + calcCarRate())

                        if (calcCarSpeed() != 0.0)
                            routeCounter.averageSpeed =
                                (routeCounter.averageSpeed + calcCarSpeed()) / 2.0

                        database.routesPerDayModel().update(routeCounter)

                    }

                    requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_routeProcess)
                }
            }

            R.id.start_button -> {
                //если есть доступ к местоположению пользователя, начинаем маршрут
                if (checkPermissions()) {
                    //запускаем сервис
                    startLocationService()

                    database.routeProgressDao().deleteAll()
                    rootView.findViewById<LinearLayout>(R.id.layout_stats).removeView(btn)

                    //меняем кнопку
                    showButtonStartStop()

                }
            }

            R.id.fab -> {
                val bottomSheetDialog =
                    BottomSheetPetrol(requireContext(), requireActivity(), this).bottomSheetDialog
                bottomSheetDialog.show()
            }

            R.id.last_week_routes -> {
                showRoutesPerDayGraphic(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    7,
                    rootView.findViewById(R.id.routes_count)
                )
            }

            R.id.last_month_routes -> {
                showRoutesPerDayGraphic(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    30,
                    rootView.findViewById(R.id.routes_count)
                )
            }

            R.id.last_half_year_routes -> {
                showRoutesPerDayGraphic(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    180,
                    rootView.findViewById(R.id.routes_count)
                )
            }

            R.id.last_year_routes -> {
                showRoutesPerDayGraphic(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    365,
                    rootView.findViewById(R.id.routes_count)
                )
            }

            R.id.last_week_rate -> {
                showRateGraphic(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    7,
                    rootView.findViewById(R.id.rates_count)
                )
            }

            R.id.last_month_rate -> {
                showRateGraphic(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    30,
                    rootView.findViewById(R.id.rates_count)
                )
            }

            R.id.last_half_year_rate -> {
                showRateGraphic(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    180,
                    rootView.findViewById(R.id.rates_count)
                )
            }

            R.id.last_year_rate -> {
                showRateGraphic(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    365,
                    rootView.findViewById(R.id.rates_count)
                )
            }

            R.id.last_week_speed -> {
                showAverageSpeed(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    7,
                    rootView.findViewById(R.id.speed_count)
                )
            }

            R.id.last_month_speed -> {
                showAverageSpeed(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    30,
                    rootView.findViewById(R.id.speed_count)
                )
            }

            R.id.last_half_year_speed -> {
                showAverageSpeed(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    180,
                    rootView.findViewById(R.id.speed_count)
                )
            }

            R.id.last_year_speed -> {
                showAverageSpeed(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    365,
                    rootView.findViewById(R.id.speed_count)
                )
            }

            R.id.last_week_distance -> {
                showDistance(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    7,
                    rootView.findViewById(R.id.distance_count)
                )
            }

            R.id.last_month_distance -> {
                showDistance(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    30,
                    rootView.findViewById(R.id.distance_count)
                )
            }

            R.id.last_half_year_distance -> {
                showDistance(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    180,
                    rootView.findViewById(R.id.distance_count)
                )
            }

            R.id.last_year_distance -> {
                showDistance(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    365,
                    rootView.findViewById(R.id.distance_count)
                )
            }

            R.id.last_week_fuel -> {
                showExpenses(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    7
                )
            }

            R.id.last_month_fuel -> {
                showExpenses(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    30
                )
            }

            R.id.last_half_year_fuel -> {
                showExpenses(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    180
                )
            }

            R.id.last_year_fuel -> {
                showExpenses(
                    database,
                    rootView,
                    requireActivity(),
                    true,
                    365
                )
            }
        }
    }

    private fun checkPermissions() : Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).toTypedArray(),
                101
            )
        } else return true
        return false
    }

}