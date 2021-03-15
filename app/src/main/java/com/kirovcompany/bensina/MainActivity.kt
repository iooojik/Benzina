package com.kirovcompany.bensina

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kirovcompany.bensina.interfaces.PreferencesUtil

@Suppress("DEPRECATION")
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

    private fun checkAddedCar(){
        //если пользователь добавил машину, то переходим на страницу с возможностью записи маршрута
        //иначе переходим на страницу с приветствием
        if (getBooleanValueFalse(preferences, staticVars.userAddedCar))
            findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_beginRoute)
        else findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_addCarInfo)
    }

    private fun setupNavigation(){
        //контроллер навигации
        //val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        //views
        val drawer : DrawerLayout = findViewById(R.id.drawer)
        AppBarConfiguration.Builder(getHomeFragment()).setDrawerLayout(drawer).build()

        //val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        //bottomNavigationView.inflateMenu(getMenu())
        //NavigationUI.setupWithNavController(bottomNavigationView, navController)

        checkAddedCar()
    }

    private fun getHomeFragment() : Int{
        return if (getBooleanValueFalse(preferences, staticVars.userAddedCar))
            R.layout.fragment_begin_route
        else R.layout.fragment_add_car_info
    }

}