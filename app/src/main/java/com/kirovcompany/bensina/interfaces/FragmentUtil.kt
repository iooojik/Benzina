package com.kirovcompany.bensina.interfaces

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.kirovcompany.bensina.LocaleHelper
import com.kirovcompany.bensina.StaticVars
import com.kirovcompany.bensina.localdb.AppDatabase
import com.kirovcompany.bensina.ui.BottomSheetPetrol
import com.kirovcompany.bensina.ui.RouteProcess
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

    fun setFabAction(fab: ExtendedFloatingActionButton, context: Context, activity: Activity, fragment: RouteProcess){
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

    fun setContextLocale(lang : String, ctx: Context) : Context{
        val context = LocaleHelper.setLocale(ctx, lang)
        return context
    }

    fun initViews()



}