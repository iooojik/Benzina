package com.kirovcompany.bensina

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.kirovcompany.bensina.interfaces.PreferencesUtil
import com.kirovcompany.bensina.localdb.AppDatabase


@Suppress("DEPRECATION", "SENSELESS_COMPARISON")
class MainActivity : AppCompatActivity(), PreferencesUtil{

    private lateinit var preferences : SharedPreferences
    private val staticVars = StaticVars()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialization()
    }

    private fun initialization(){
        preferences = getSharedPreferences(this)
        setupNavigation()
    }

    private fun goToStartFragment(){
        //если пользователь добавил машину, то переходим на страницу с возможностью записи маршрута
        //иначе переходим на страницу с приветствием
        //если пользователь уже начал запись, то переходим на фрагемент с маршрутом
        val database = AppDatabase.getAppDataBase(applicationContext)
        if (getBooleanValueFalse(preferences, staticVars.userAddedCar)){
            if (database != null){
                if (database.serviceDao().get() != null){
                    val running = database.serviceDao().get().status
                    if (running == true) {
                        findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_routeProcess)
                    } else findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_beginRoute)
                } else findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_addCarInfo)
            } else findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_beginRoute)
        }
        else findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_addCarInfo)
    }

    private fun setupNavigation(){

        //views
        val drawer : DrawerLayout = findViewById(R.id.drawer)
        AppBarConfiguration.Builder(getHomeFragment()).setDrawerLayout(drawer).build()

        goToStartFragment()
    }

    private fun getHomeFragment() : Int{
        return if (getBooleanValueFalse(preferences, staticVars.userAddedCar))
            R.layout.fragment_begin_route
        else R.layout.fragment_add_car_info
    }

}