package com.example.kimcoach.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.kimcoach.R
import com.example.kimcoach.common.Constants
import com.example.kimcoach.common.GlideModule
import com.example.kimcoach.databinding.ActivitySensorBinding
import com.example.kimcoach.room.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SensorActivity : Activity(), SensorEventListener {
    private lateinit var binding: ActivitySensorBinding
    private lateinit var db: SensorDataBase
    private lateinit var dao: SensorDao
    private var accList: List<AcceleratorEntity>? = null
    private var gyroList: List<GyroEntity>? = null
    private var heartBeatList: List<HeartBeatEntity>? = null
    private var gpsList: List<GpsEntity>? = null
    private lateinit var sensorManager: SensorManager

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this).load(R.drawable.running).circleCrop().into(binding.ivSensorOnLoading)
        registerSensor()
        db = SensorDataBase.getInstance(applicationContext)!!
        dao = db.sensorDao()
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteGpsTable()
            dao.deleteGyroTable()
            dao.deleteAcceleratorTable()
            dao.deleteHeartBeatTable()
        }
        registerStopBtn()
    }

    private fun registerMoveToUploadBtn(){
        binding.btnMoveToUpload.setOnClickListener {
            moveToUpload()
        }
    }

    private fun registerStopBtn() {
        binding.btnStopSensor.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                launch {
                    accList = (dao.getAllAcceleratorData())
                    gyroList = dao.getAllGyroData()
                    heartBeatList = dao.getAllHeartBeatData()
                    gpsList = dao.getAllGpsData()
                }.join()

                launch {
                    exportDatabaseToCSVFile()
                }.join()



            }
            Toast.makeText(this, "경기 기록 완료", LENGTH_SHORT).show()
            binding.btnStopSensor.isVisible = false
            binding.btnMoveToUpload.isVisible = true
            binding.btnMoveToUpload.isEnabled = true

            registerMoveToUploadBtn()
        }
    }

    private fun moveToUpload(){
        val intent = Intent(this, UploadActivity::class.java )
        startActivity(intent)
    }

    private fun getCSVFileName(): String {
        return Constants.SENSOR_FILE_NAME
    }

    private fun exportDatabaseToCSVFile() {
        val csvFile = generateFile(this, getCSVFileName())
        if (csvFile != null) {
            exportSensorDataToCsv(csvFile)


            try {
                val intent = goToFileIntent(this, csvFile)
                startActivity(intent)
            } catch (e: Exception) {

            }
        } else {
            Toast.makeText(this, "CSV 파일 생성 실패", Toast.LENGTH_LONG).show()
        }
    }

    private fun generateFile(context: Context, fileName: String): File? {
        val csvFile = File(context.filesDir, fileName)
        csvFile.createNewFile()

        return if (csvFile.exists()) {
            csvFile.delete()
            csvFile
        } else {
            null
        }
    }

    private fun goToFileIntent(context: Context, file: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val contentUri =
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val mimeType = context.contentResolver.getType(contentUri)
        intent.setDataAndType(contentUri, mimeType)
        intent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        return intent
    }

    private fun exportSensorDataToCsv(csvFile: File) {
        csvWriter().open(csvFile, append = false) {
            writeRow(listOf("ACC [id]", "[Time]", "[X]", "[Y]", "[Z]"))
            accList?.forEachIndexed { index, data ->
                writeRow(listOf(index, data.timestamp, data.x, data.y, data.z))
            }


            writeRow(listOf("GYRO [id]", "[Time]", "[X]", "[Y]", "[Z]"))
            gyroList?.forEachIndexed { index, gyroEntity ->
                writeRow(
                    listOf(
                        index,
                        gyroEntity.timestamp,
                        gyroEntity.x,
                        gyroEntity.y,
                        gyroEntity.z
                    )
                )
            }


            writeRow(listOf("HeartBeat [id]", "[Time]", "Beat"))
            heartBeatList?.forEachIndexed { index, heartBeatEntity ->
                writeRow(listOf(index, heartBeatEntity.timestamp, heartBeatEntity.beat))
            }



            writeRow(listOf("GPS [id]", "[Time]", "[X]", "[Y]"))
            gpsList?.forEachIndexed { index, gpsEntity ->
                writeRow(
                    listOf(
                        index,
                        gpsEntity.timestamp,
                        gpsEntity.latitude,
                        gpsEntity.longitude
                    )
                )
            }

        }


    }

    @SuppressLint("MissingPermission")
    private fun registerGps() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 2 * 1000

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            dao.insertGps(
                                GpsEntity(
                                    0,
                                    System.currentTimeMillis().toString(),
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        }
                    }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        );

    }

    private fun registerSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        registerGps()
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            CoroutineScope(Dispatchers.IO).launch {
                dao.insertGyro(
                    GyroEntity(
                        0,
                        System.currentTimeMillis().toString(),
                        event.values[0].toDouble(),
                        event.values[1].toDouble(),
                        event.values[2].toDouble()
                    )
                )
            }
        }

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            CoroutineScope(Dispatchers.IO).launch {
                dao.insertAccelerator(
                    AcceleratorEntity(
                        0,
                        System.currentTimeMillis().toString(),
                        event.values[0].toDouble(),
                        event.values[1].toDouble(),
                        event.values[2].toDouble()
                    )
                )
            }
        }

        if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
            CoroutineScope(Dispatchers.IO).launch {
                dao.insertHeartBeat(
                    HeartBeatEntity(
                        0,
                        System.currentTimeMillis().toString(),
                        event.values[0].toInt()
                    )
                )
            }
        }
    }

    override fun onAccuracyChanged(event: Sensor?, p1: Int) {
        return
    }


    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

}