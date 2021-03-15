package com.kirovcompany.bensina.localdb.routeprogress

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RouteProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(routeProgressModel: RouteProgressModel)

    @Query("SELECT * FROM routeprogressmodel")
    fun getAll() : List<RouteProgressModel>

}