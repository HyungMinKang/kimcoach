package com.example.kimcoach.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "HeartBeat")
data class HeartBeatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val timestamp: String,
    val beat: Int
)
