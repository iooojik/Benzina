package com.kirovcompany.benzina.interfaces

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.kirovcompany.benzina.StaticVars

interface PreferencesUtil {

    fun getSharedPreferences(activity: Activity) : SharedPreferences {
        //получаение сохранённых настроек приложения
        return activity.getSharedPreferences(StaticVars.preferencesName, Context.MODE_PRIVATE)
    }

    fun saveStringToSharedPreferences(preferences: SharedPreferences, name : String, value : String) {
        //сохранение значения String
        preferences.edit().putString(name, value).apply()
    }

    fun getStringValue(preferences: SharedPreferences, key : String, defValue : String) : String{
        //получения String значения
        return preferences.getString(key, defValue).toString()
    }

    fun getStringValue(preferences: SharedPreferences, key : String) : String{
        //получения String значения
        return preferences.getString(key, "").toString()
    }

    fun saveIntToSharedPreferences(preferences: SharedPreferences, name : String, value : Int) {
        //сохранение значения Int
        preferences.edit().putInt(name, value).apply()
    }

    fun getIntValue(preferences: SharedPreferences, key : String, defValue : Int) : Int{
        //получения Int значения
        return preferences.getInt(key, defValue)
    }

    fun getIntValue(preferences: SharedPreferences, key : String) : Int{
        //получения Int значения
        return preferences.getInt(key, 0)
    }

    fun saveBooleanToSharedPreferences(preferences: SharedPreferences, name : String, value : Boolean) {
        //сохранение значения Boolean
        preferences.edit().putBoolean(name, value).apply()
    }

    fun getBooleanValue(preferences: SharedPreferences, key : String, defVal : Boolean) : Boolean{
        //получения Boolean значения
        return preferences.getBoolean(key, defVal)
    }

    fun getBooleanValueTrue(preferences: SharedPreferences, key : String) : Boolean{
        //получения Boolean значения
        return preferences.getBoolean(key, true)
    }

    fun getBooleanValueFalse(preferences: SharedPreferences, key : String) : Boolean{
        //получения Boolean значения
        return preferences.getBoolean(key, false)
    }

}