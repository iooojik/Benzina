package com.kirovcompany.benzina.localdb.petrol

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PetrolDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(petrolModel: PetrolModel)

    @Query("SELECT * FROM petrolmodel")
    fun getAll() : List<PetrolModel>

    @Query("DELETE FROM petrolmodel")
    fun deleteAll()

    @Query("SELECT * FROM petrolmodel WHERE date = :date")
    fun getByDate(date : String) : List<PetrolModel>
}