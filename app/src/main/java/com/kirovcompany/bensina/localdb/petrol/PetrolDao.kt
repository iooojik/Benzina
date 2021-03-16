package com.kirovcompany.bensina.localdb.petrol

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
}