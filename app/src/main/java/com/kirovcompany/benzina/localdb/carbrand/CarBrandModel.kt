package com.kirovcompany.benzina.localdb.carbrand

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CarBrandModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id : Long? = null,

    @ColumnInfo(name = "car_brand")
    var carBrand : String

    )



