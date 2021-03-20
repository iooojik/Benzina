package com.kirovcompany.benzina.localdb.routesperday

import androidx.room.*

@Dao
interface RoutesPerDayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(routesPerDayModel: RoutesPerDayModel)

    @Query("SELECT * FROM routesperdaymodel WHERE date = :date")
    fun getByDate(date : String) : RoutesPerDayModel

    @Update
    fun update(routesPerDayModel: RoutesPerDayModel)

    @Query("SELECT * FROM routesperdaymodel")
    fun getAll() : List<RoutesPerDayModel>

    @Query("DELETE FROM routesperdaymodel")
    fun deleteAll()
}