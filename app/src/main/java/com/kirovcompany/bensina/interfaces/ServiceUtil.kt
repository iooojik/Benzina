package com.kirovcompany.bensina.interfaces

import android.content.Context
import com.kirovcompany.bensina.localdb.AppDatabase

interface ServiceUtil : PreferencesUtil {

    fun getAppDatabase(context: Context) : AppDatabase {
        //получение локальной базы данных
        return AppDatabase.getAppDataBase(context)!!
    }
}