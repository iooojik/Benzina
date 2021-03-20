package com.kirovcompany.benzina.localdb.carbrand

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CarBrandDao {
    /*
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(messageLocalModel: MessageLocalModel)

    @Update
    fun update(messageLocalModel: MessageLocalModel)

    @Delete
    fun delete(messageLocalModel: MessageLocalModel)

    @Query("SELECT * FROM messagelocalmodel")
    fun getAll() : List<MessageLocalModel>

    @Query("SELECT * FROM messagelocalmodel WHERE room_unique_id = :uniqueID")
    fun getAllByUniqueID(uniqueID : String) : List<MessageLocalModel>
     */
    @Query("SELECT * FROM carbrandmodel WHERE _id = :id")
    fun getByID(id : String) : CarBrandModel

    @Query("SELECT * FROM carbrandmodel")
    fun getAll() : List<CarBrandModel>
}