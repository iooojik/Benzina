package com.kirovcompany.bensina.localdb.car

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CarModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id : Long? = null,

    @ColumnInfo(name = "car_brand")
    var carBrand : String,

    @ColumnInfo(name = "car_model")
    var carModel : String,

    @ColumnInfo(name = "car_year")
    var carYear : String,

    @ColumnInfo(name = "car_odometer")
    var carOdometer : String,

    @ColumnInfo(name = "car_engine_amount")
    var carEngineAmount : String,

    @ColumnInfo(name = "car_rate")
    var carRate : String

)