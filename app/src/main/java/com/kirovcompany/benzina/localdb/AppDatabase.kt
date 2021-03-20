package com.kirovcompany.benzina.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fstyle.library.helper.AssetSQLiteOpenHelperFactory
import com.kirovcompany.benzina.localdb.car.CarModel
import com.kirovcompany.benzina.localdb.car.CarModelDao
import com.kirovcompany.benzina.localdb.carbrand.CarBrandDao
import com.kirovcompany.benzina.localdb.carbrand.CarBrandModel
import com.kirovcompany.benzina.localdb.petrol.PetrolDao
import com.kirovcompany.benzina.localdb.petrol.PetrolModel
import com.kirovcompany.benzina.localdb.routeprogress.RouteProgressDao
import com.kirovcompany.benzina.localdb.routeprogress.RouteProgressModel
import com.kirovcompany.benzina.localdb.routesperday.RoutesPerDayDao
import com.kirovcompany.benzina.localdb.routesperday.RoutesPerDayModel
import com.kirovcompany.benzina.localdb.service.ServiceDao
import com.kirovcompany.benzina.localdb.service.ServiceModel
import com.kirovcompany.benzina.localdb.timer.TimerDao
import com.kirovcompany.benzina.localdb.timer.TimerModel

@Database(entities = [CarBrandModel::class, ServiceModel::class,
    RouteProgressModel::class, CarModel::class, TimerModel::class,
    PetrolModel::class, RoutesPerDayModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun carBrandDao() : CarBrandDao
    abstract fun serviceDao() : ServiceDao
    abstract fun routeProgressDao() : RouteProgressDao
    abstract fun carModelDao() : CarModelDao
    abstract fun timerDao() : TimerDao
    abstract fun petrolDao() : PetrolDao
    abstract fun routesPerDayModel() : RoutesPerDayDao

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