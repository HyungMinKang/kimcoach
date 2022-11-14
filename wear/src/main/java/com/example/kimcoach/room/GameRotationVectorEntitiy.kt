package com.example.kimcoach.room

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "GameRotationVector")
data class GameRotationVectorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val timestamp:String,
    val x: Double,
    val y: Double,
    val z: Double
)