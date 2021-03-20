package com.kirovcompany.benzina.localdb.car

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CarModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(carModel: CarModel)

    @Query("SELECT * FROM carmodel ORDER BY _id DESC LIMIT 1")
    fun getLast() : CarModel

    @Query("DELETE FROM carmodel")
    fun deleteAll()
}