package com.kirovcompany.benzina.localdb.service

import androidx.room.*

@Dao
interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(serviceModel: ServiceModel)

    @Query("SELECT * FROM servicemodel ORDER BY _id DESC LIMIT 1")
    fun get() : ServiceModel

    @Update
    fun update(serviceModel: ServiceModel)

    @Query("DELETE FROM servicemodel")
    fun deleteAll()
}