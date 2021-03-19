package com.kirovcompany.bensina.localdb.timer

import androidx.room.*

@Dao
interface TimerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(timerModel: TimerModel)

    @Query("SELECT * FROM timermodel ORDER BY _id DESC LIMIT 1")
    fun get() : TimerModel

    @Update
    fun update(timerModel: TimerModel)

    @Query("DELETE FROM timermodel")
    fun deleteAll()
}