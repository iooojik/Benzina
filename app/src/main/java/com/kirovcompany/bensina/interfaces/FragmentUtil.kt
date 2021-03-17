package com.kirovcompany.bensina.interfaces

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.kirovcompany.bensina.localdb.AppDatabase
import com.kirovcompany.bensina.ui.BeginRoute
import com.kirovcompany.bensina.ui.BottomSheetPetrol
import java.text.SimpleDateFormat
import java.util.*

interface FragmentUtil : PreferencesUtil {

    fun getAppDatabase(context: Context) : AppDatabase {
        //получение локальной базы данных
        return AppDatabase.getAppDataBase(context)!!
    }

    fun blockGoBack(activity: ComponentActivity, fragment: Fragment){
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        activity.onBackPressedDispatcher.addCallback(fragment.viewLifecycleOwner, callback)
    }

    fun setFabAction(fab: ExtendedFloatingActionButton, context: Context, activity: Activity, fragment: BeginRoute?){
        fab.setOnClickListener {
            val bottomSheetAddChild = BottomSheetPetrol(context, activity, fragment).bottomSheetDialog
            bottomSheetAddChild.show()
        }
    }

    fun getCurrentDate() : String {
        // Текущее время
        val currentDate = Date()
        // Форматирование времени как "день.месяц.год"
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(currentDate)
    }

    fun roundDouble(double : Double) : String{
        return String.format("%.2f", double)
    }

    fun initViews()

}