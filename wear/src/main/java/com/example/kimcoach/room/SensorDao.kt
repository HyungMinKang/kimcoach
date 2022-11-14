package com.example.kimcoach.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SensorDao {

    @Insert
    fun insertAccelerator(entity: AcceleratorEntity)

    @Insert
    fun insertGyro(entity: GyroEntity)

    @Insert
    fun insertGps(entity: GpsEntity)

    @Insert
    fun insertHeartBeat(entity: HeartBeatEntity)

    @Insert
    fun insertGameRotationVector(entity: GameRotationVectorEntity)

    @Query("DELETE FROM Accelerator")
    fun deleteAcceleratorTable()

    @Query("DELETE FROM Gps")
    fun deleteGpsTable()

    @Query("DELETE FROM Gyro")
    fun deleteGyroTable()

    @Query("DELETE FROM HeartBeat")
    fun deleteHeartBeatTable()

    @Query("DELETE FROM GameRotationVector")
    fun deleteGameRotationVectorTable()

    @Query("SELECT * FROM Accelerator")
    fun getAllAcceleratorData():List<AcceleratorEntity>

    @Query("SELECT * FROM Gps")
    fun getAllGpsData():List<GpsEntity>

    @Query("SELECT * FROM Gyro")
    fun getAllGyroData():List<GyroEntity>

    @Query("SELECT * FROM HeartBeat")
    fun getAllHeartBeatData():List<HeartBeatEntity>

    @Query("SELECT * FROM GameRotationVector")
    fun getAllGameRotationVector():List<GameRotationVectorEntity>



}