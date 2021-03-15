package com.kirovcompany.bensina.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fstyle.library.helper.AssetSQLiteOpenHelperFactory
import com.kirovcompany.bensina.localdb.carbrand.CarBrandDao
import com.kirovcompany.bensina.localdb.carbrand.CarBrandModel

@Database(entities = [CarBrandModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun carBrandDao() : CarBrandDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null){
                synchronized(AppDatabase::class){

                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "database.db")
                        .openHelperFactory(AssetSQLiteOpenHelperFactory())
                        .allowMainThreadQueries()
                        .build()

                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}