package com.kirovcompany.benzina.localdb.timer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TimerModel (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        val id : Long? = null,

        @ColumnInfo(name = "time_seconds")
        var seconds : Long? = null
)