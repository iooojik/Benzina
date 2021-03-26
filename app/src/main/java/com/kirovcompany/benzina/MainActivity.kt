package com.kirovcompany.benzina

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kirovcompany.benzina.interfaces.AdUtil
import com.kirovcompany.benzina.interfaces.FragmentUtil
import com.kirovcompany.benzina.interfaces.PreferencesUtil
import com.kirovcompany.benzina.localdb.AppDatabase
import java.util.*


@Suppress("SENSELESS_COMPARISON", "IMPLICIT_BOXING_IN_IDENTITY_EQUALS")
class MainActivity : AppCompatActivity(), PreferencesUtil, FragmentUtil, AdUtil{

    private lateinit var preferences : SharedPreferences

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(base!!, "ru"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences(StaticVars.preferencesName, Context.MODE_PRIVATE)

        if (preferences.getInt(StaticVars.firstAppStartUP, 0) == 0){
            preferences.edit().putInt(StaticVars.firstAppStartUP, 1).apply()
            restart()
        }else {
            setContentView(R.layout.activity_main)
            if (!preferences.getBoolean(StaticVars.preferencesLanguageChanged, false))
                showAd()
            else preferences.edit().putBoolean(StaticVars.preferencesLanguageChanged, false).apply()
            initialization()
        }

    }

    private fun restart(){
        val intent = intent
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        //preferences.edit().putInt(StaticVars.firstAppStartUP, 0).apply()
    }

    private fun initialization(){
        setupNavigation()
    }

    private fun showAd(){

        MobileAds.initialize(this) {}
        if (preferences.getBoolean(StaticVars.userAddedCar, false)) {
            //показываем баннер

            showBanner(this)

            //показываем видео-рекламу
            val database = AppDatabase.getAppDataBase(applicationContext)

            if (database?.serviceDao()?.get() != null) {

                if (!database.serviceDao().get().status) {
                    showInterstitialAd(applicationContext, this, false)
                }

            } else showInterstitialAd(applicationContext, this, false)
        }

    }

    private fun goToStartFragment(){
        //если пользователь добавил машину, то переходим на страницу с возможностью записи маршрута
        //иначе переходим на страницу с приветствием
        //если пользователь уже начал запись, то переходим на фрагемент с маршрутом
        val database = AppDatabase.getAppDataBase(applicationContext)

        if (getBooleanValueFalse(preferences, StaticVars.userAddedCar)){

            if (database != null){

                if (database.serviceDao().get() != null){

                    val running = database.serviceDao().get().status

                    if (running) {

                        findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_routeProcess)

                    } else findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_routeProcess)

                } else if(database.serviceDao().get() == null || getBooleanValueFalse(
                        preferences,
                        StaticVars.userAddedCar
                    )) {

                    findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_routeProcess)

                }  else findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_helloFragment)

            } else findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_routeProcess)

        } else findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_helloFragment)
    }

    private fun setupNavigation(){
        //views
        val drawer : DrawerLayout = findViewById(R.id.drawer)
        AppBarConfiguration.Builder(getHomeFragment()).setDrawerLayout(drawer).build()
        goToStartFragment()
    }

    private fun getHomeFragment() : Int{
        return if (getBooleanValueFalse(preferences, StaticVars.userAddedCar))
            R.layout.fragment_route_process
        else R.layout.fragment_add_car_info
    }

    private fun daysBetween(d1: Date, d2: Date): Long {
        return ((d2.time - d1.time) / (1000 * 60 * 60 * 24))
    }

    override fun initViews() {

    }

}