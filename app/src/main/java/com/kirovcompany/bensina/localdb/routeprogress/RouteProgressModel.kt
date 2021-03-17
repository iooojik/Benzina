package com.kirovcompany.bensina.localdb.routeprogress

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RouteProgressModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id : Long? = null,

    @ColumnInfo(name = "speed")
    var speed : String,

    @ColumnInfo(name = "distance")
    var distance : String,

    @ColumnInfo(name = "car_rate")
    var carRate : String
)