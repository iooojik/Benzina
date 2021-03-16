package com.kirovcompany.bensina.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fstyle.library.helper.AssetSQLiteOpenHelperFactory
import com.kirovcompany.bensina.localdb.car.CarModel
import com.kirovcompany.bensina.localdb.car.CarModelDao
import com.kirovcompany.bensina.localdb.carbrand.CarBrandDao
import com.kirovcompany.bensina.localdb.carbrand.CarBrandModel
import com.kirovcompany.bensina.localdb.routeprogress.RouteProgressDao
import com.kirovcompany.bensina.localdb.routeprogress.RouteProgressModel
import com.kirovcompany.bensina.localdb.service.ServiceDao
import com.kirovcompany.bensina.localdb.service.ServiceModel

@Database(entities = [CarBrandModel::class, ServiceModel::class,
    RouteProgressModel::class, CarModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun carBrandDao() : CarBrandDao
    abstract fun serviceDao() : ServiceDao
    abstract fun routeProgressDao() : RouteProgressDao
    abstract fun carModelDao() : CarModelDao

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