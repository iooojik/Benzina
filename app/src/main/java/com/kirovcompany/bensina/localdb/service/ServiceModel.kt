package com.kirovcompany.bensina.localdb.service

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ServiceModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id : Long? = null,

    @ColumnInfo(name = "status")
    var status : Boolean? = null
)