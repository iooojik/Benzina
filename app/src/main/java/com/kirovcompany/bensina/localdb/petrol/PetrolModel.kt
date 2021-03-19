package com.kirovcompany.bensina.localdb.petrol

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class PetrolModel (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id : Long? = null,

    @ColumnInfo(name = "currency")
    var currency : String,

    @ColumnInfo(name = "price")
    var price : Double,

    @ColumnInfo(name = "amount")
    var amount : Double,

    @ColumnInfo(name = "date")
    var date : String
)