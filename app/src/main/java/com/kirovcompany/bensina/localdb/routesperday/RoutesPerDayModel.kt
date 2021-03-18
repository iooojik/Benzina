package com.kirovcompany.bensina.localdb.routesperday

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RoutesPerDayModel (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        val id : Long? = null,

        @ColumnInfo(name = "date")
        var date : String,

        @ColumnInfo(name = "num")
        var num : Long,

        @ColumnInfo(name = "average_car_rate")
        var averageCarRate : Double,

        @ColumnInfo(name = "average_speed")
        var averageSpeed : Double,

        @ColumnInfo(name = "distance")
        var distance : Double
)