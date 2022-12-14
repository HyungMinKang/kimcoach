package com.example.kimcoach.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.*
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.example.kimcoach.R
import com.example.kimcoach.common.Constants
import com.example.kimcoach.databinding.ActivitySensorBinding
import com.example.kimcoach.room.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import java.io.File

class SensorActivity : Activity(), SensorEventListener {
    private lateinit var binding: ActivitySensorBinding
    private lateinit var db: SensorDataBase
    private lateinit var dao: SensorDao
    private var accList: List<AcceleratorEntity>? = null
    private var gyroList: List<GyroEntity>? = null
    private var startTime = 0L
    private var duration = 5L
    private var accCount = 0
    private var gyroCount = 0
    private var vectorCount = 0
    private var heartBeatList: List<HeartBeatEntity>? = null
    private var gpsList: List<GpsEntity>? = null
    private var gameRvList: List<GameRotationVectorEntity>? = null
    private lateinit var sensorManager: SensorManager

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerSensor()
        initDataBase()
        registerStopBtn()
    }

    private val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("Error", ": ${throwable.message}")
        }

    private fun initDataBase() {
        db = SensorDataBase.getInstance(applicationContext)!!
        dao = db.sensorDao()
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
            dao.deleteGpsTable()
            dao.deleteGyroTable()
            dao.deleteAcceleratorTable()
            dao.deleteGameRotationVectorTable()
        }
    }

    private fun registerMoveToUploadBtn() {
        binding.btnMoveToUpload.setOnClickListener {
            it.isEnabled = false
            moveToUpload()
        }
    }

    private fun registerStopBtn() {
        binding.btnStopSensor.setOnClickListener {
            sensorManager.unregisterListener(this)
            CoroutineScope(Dispatchers.IO).launch {
                launch {
                    accList = (dao.getAllAcceleratorData())
                    gyroList = dao.getAllGyroData()
                    gpsList = dao.getAllGpsData()
                    gameRvList = dao.getAllGameRotationVector()
                }.join()

                launch {
                    exportDatabaseToCSVFile()
                }.join()

            }
            Toast.makeText(this, getString(R.string.sensor_finish_message), LENGTH_SHORT).show()
            binding.btnStopSensor.isVisible = false
            //binding.pgSensor.isVisible = false
            binding.btnMoveToUpload.isVisible = true
            binding.btnMoveToUpload.isEnabled = true

            registerMoveToUploadBtn()
        }
    }

    private fun moveToUpload() {
        val intent = Intent(this, UploadActivity::class.java)
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
            Toast.makeText(
                this,
                getString(R.string.sensor_file_make_fail_message),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun generateFile(context: Context, fileName: String): File? {
        val csvFile = File(context.filesDir, fileName)
        csvFile.createNewFile()

        return csvFile
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
            writeRow(
                listOf(
                    getString(R.string.acc_table_name),
                    getString(R.string.table_time),
                    getString(R.string.table_x),
                    getString(R.string.table_y),
                    getString(R.string.table_z)
                )
            )
            accList?.forEachIndexed { index, acceleratorEntity ->
                writeRow(
                    listOf(
                        index,
                        acceleratorEntity.timestamp,
                        acceleratorEntity.x,
                        acceleratorEntity.y,
                        acceleratorEntity.z
                    )
                )
            }

            writeRow(
                 listOf(
                    getString(R.string.gyro_table_name),
                    getString(R.string.table_time),
                    getString(R.string.table_x),
                    getString(R.string.table_y),
                    getString(R.string.table_z)
                )
            )
            gyroList?.forEachIndexed { index, gyroEntity ->
                writeRow(
                    listOf(index, gyroEntity.timestamp, gyroEntity.x, gyroEntity.y, gyroEntity.z)
                )
            }

            writeRow(
                listOf(
                    getString(R.string.gps_table_name),
                    getString(R.string.table_time),
                    getString(R.string.gps_latitude),
                    getString(R.string.gps_longitude)
                )
            )
            gpsList?.forEachIndexed { index, gpsEntity ->
                writeRow(
                    listOf(index, gpsEntity.timestamp, gpsEntity.latitude, gpsEntity.longitude)
                )
            }

            writeRow(
                listOf(
                    "GAME RV",
                    getString(R.string.table_time),
                    getString(R.string.table_x),
                    getString(R.string.table_y),
                    getString(R.string.table_z)
                )
            )

            gameRvList?.forEachIndexed { index, grvEntity ->
                writeRow(
                    listOf(index, grvEntity.timestamp, grvEntity.x, grvEntity.y, grvEntity.z)
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
        locationRequest.priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 2
        locationRequest.maxWaitTime = 2
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
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
        )

    }

    private fun registerSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        registerGps()
        startTime = System.currentTimeMillis()


        //????????? ??????
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                5,
                10
            )
        }

        //????????? ??????
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED)?.also {
            sensorManager.registerListener(

                this,
                it,
                5,
                10
            )
        }

        //RV ??????
        sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)?.also {
            sensorManager.registerListener(
                this,
                it,
                5,
                10
            )
        }

    }

    //????????? ?????? ????????? ??????
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {
            val time = (startTime + duration * gyroCount).toString()
            CoroutineScope(Dispatchers.IO).launch {
                dao.insertGyro(
                    GyroEntity(
                        0,
                        time,
                        event.values[0].toDouble(),
                        event.values[1].toDouble(),
                        event.values[2].toDouble()
                    )
                )
            }
            gyroCount++
        }

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val time = (startTime + duration * accCount).toString()
            CoroutineScope(Dispatchers.IO).launch {
                dao.insertAccelerator(
                    AcceleratorEntity(
                        0,
                        time,
                        event.values[0].toDouble(),
                        event.values[1].toDouble(),
                        event.values[2].toDouble()
                    )
                )
            }
            accCount++
        }

        if (event?.sensor?.type == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            val time = (startTime + duration * vectorCount).toString()
            CoroutineScope(Dispatchers.IO).launch {
                dao.insertGameRotationVector(
                    GameRotationVectorEntity(
                        0,
                        time,
                        event.values[0].toDouble(),
                        event.values[1].toDouble(),
                        event.values[2].toDouble()
                    )
                )
            }
            vectorCount++
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