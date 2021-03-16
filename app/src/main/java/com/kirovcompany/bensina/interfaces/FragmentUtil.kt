package com.kirovcompany.bensina.interfaces

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.kirovcompany.bensina.localdb.AppDatabase

interface FragmentUtil : PreferencesUtil {

    fun getAppDatabase(context: Context) : AppDatabase {
        //получение локальной базы данных
        return AppDatabase.getAppDataBase(context)!!
    }

    fun blockGoBack(activity : ComponentActivity, fragment : Fragment){
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        activity.onBackPressedDispatcher.addCallback(fragment.viewLifecycleOwner, callback)
    }

    fun initViews()

}