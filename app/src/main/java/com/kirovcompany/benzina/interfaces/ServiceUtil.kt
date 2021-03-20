package com.kirovcompany.benzina.interfaces

import android.content.Context
import com.kirovcompany.benzina.localdb.AppDatabase

interface ServiceUtil : PreferencesUtil {

    fun getAppDatabase(context: Context) : AppDatabase {
        //получение локальной базы данных
        return AppDatabase.getAppDataBase(context)!!
    }
}