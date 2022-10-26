package com.example.kimcoach.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Gps")
data class GpsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val timestamp: String,
    val latitude: Double,
    val longitude: Double
)