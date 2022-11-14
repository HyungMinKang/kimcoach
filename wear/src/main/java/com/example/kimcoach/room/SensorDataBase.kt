package com.example.kimcoach.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kimcoach.R


@Database(entities = [AcceleratorEntity::class, GpsEntity::class, GyroEntity::class, HeartBeatEntity::class, GameRotationVectorEntity::class], version =2,exportSchema = true )
abstract class SensorDataBase : RoomDatabase() {
    abstract fun sensorDao(): SensorDao

    companion object {
        private var instance: SensorDataBase? = null

        fun getInstance(context: Context): SensorDataBase? {
            if (instance == null) {
                synchronized(SensorDataBase::class) {
                    instance = Room.databaseBuilder(context, SensorDataBase::class.java, context.getString(R.string.db_name)).fallbackToDestructiveMigration().build() }
            }
            return instance
        }
    }
}