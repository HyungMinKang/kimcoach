package com.example.kimcoach

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Movie
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Looper
import android.telephony.CarrierConfigManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.kimcoach.databinding.ActivityMainBinding
import com.example.kimcoach.room.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import java.util.*


class MainActivity : Activity(), SensorEventListener, DataClient.OnDataChangedListener {

    private val REQUEST_CODE_PERMISSION = 1001
    private lateinit var db: SensorDataBase
    private lateinit var dao: SensorDao
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var accList: List<AcceleratorEntity>? = null
    private var gyroList: List<GyroEntity>? = null
    private var heartBeatList: List<HeartBeatEntity>? = null
    private var gpsList: List<GpsEntity>? = null
    private var datapath = "/data_path"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BODY_SENSORS
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        db = SensorDataBase.getInstance(applicationContext)!!
        dao = db.sensorDao()
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteGpsTable()
            dao.deleteGyroTable()
            dao.deleteAcceleratorTable()
            dao.deleteHeartBeatTable()
        }
        setContentView(binding.root)
        registerStartBtn()
        registerStopBtn()

    }

    private fun registerStartBtn() {
        binding.btnStartSensor.setOnClickListener {
            binding.btnStopSensor.isEnabled = true
            it.isEnabled = false
            registerSensor()
        }
    }

    private fun registerStopBtn() {
        binding.btnStopSensor.setOnClickListener {
            binding.btnStartSensor.isEnabled = true
            it.isEnabled = false
            CoroutineScope(Dispatchers.IO).launch {
                accList = (dao.getAllAcceleratorData())
                gyroList = dao.getAllGyroData()
                heartBeatList = dao.getAllHeartBeatData()
                gpsList = dao.getAllGpsData()

            }
            exportDatabaseToCSVFile()
            //send DB to mobile
        }
    }

    private fun getCSVFileName(): String = "SensorData.csv"

    private fun exportDatabaseToCSVFile() {
        val csvFile = generateFile(this, getCSVFileName())
        if (csvFile != null) {
            exportSensroDataToCsv(csvFile)
            Toast.makeText(this, "CSV 파일 생성 성공", Toast.LENGTH_LONG).show()

            try {
                val intent = goToFileIntent(this, csvFile)
                startActivity(intent)
            } catch (e: Exception) {

            }
        } else {
            Toast.makeText(this, "CSV 파일 생성 실패", Toast.LENGTH_LONG).show()
        }
    }

    fun generateFile(context: Context, fileName: String): File? {
        val csvFile = File(context.filesDir, fileName)
        csvFile.createNewFile()

        return if (csvFile.exists()) {
            csvFile
        } else {
            null
        }
    }

    fun goToFileIntent(context: Context, file: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val contentUri =
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val mimeType = context.contentResolver.getType(contentUri)
        intent.setDataAndType(contentUri, mimeType)
        intent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        return intent
    }

    fun exportSensroDataToCsv(csvFile: File) {
        csvWriter().open(csvFile, append = false) {
            // Header
            writeRow(listOf("ACC [id]", "[Time]", "[X]", "[Y]", "[Z]"))
            accList?.forEachIndexed { index, data ->
                writeRow(listOf(index, data.timestamp, data.x, data.y, data.z))
            }
            writeRow(listOf("GYRO [id]", "[Time]", "[X]", "[Y]", "[Z]"))
            gyroList?.forEachIndexed{index, gyroEntity ->
                writeRow(listOf(index, gyroEntity.timestamp, gyroEntity.x, gyroEntity.y, gyroEntity.z))
            }

            writeRow(listOf("GPS [id]", "[Time]", "[X]", "[Y]"))
            gpsList?.forEachIndexed { index, gpsEntity ->
                writeRow(listOf(index, gpsEntity.timestamp, gpsEntity.latitude, gpsEntity.longitude))
            }

            writeRow(listOf("HeartBeat [id]", "[Time]", "Beat"))
            heartBeatList?.forEachIndexed { index, heartBeatEntity ->
                writeRow(listOf(index, heartBeatEntity.timestamp, heartBeatEntity.beat))
            }
        }
    }

    private fun isAllGranted(): Boolean = REQUIRED_PERMISSIONS.all { permission ->
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermission() {
        if (isAllGranted()) {
            println("All Permission Granted")
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 모든 권한 취득 완료
            } else {
                if (shouldShowRequestPermissionRationale(REQUIRED_PERMISSIONS[0])) {
                    //권한 요청에 대한 합리적 설명을 사용자에게 제공
                    requestPermission()
                } else {
                    // 권한 요청이 거부됨
                }
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
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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

                        Log.d("Test", "GPS Location changed, Latitude")
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
        checkPermission()
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

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }


    private fun hasGps(): Boolean =
        packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        Log.d(TAG, "onDataChanged: $dataEventBuffer")
        for (event in dataEventBuffer) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (datapath == path) {
                    val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                    val message = dataMapItem.dataMap.getString("message")
                    Log.v(TAG, "Wear activity received message: $message")

                } else {
                    Log.e(TAG, "Unrecognized path: $path")
                }
            } else if (event.type == DataEvent.TYPE_DELETED) {
                Log.v(TAG, "Data deleted : " + event.dataItem.toString())
            } else {
                Log.e(TAG, "Unknown data event Type = " + event.type)
            }
        }
    }

    private fun sendData(message: String) {
        val dataMap = PutDataMapRequest.create(datapath)
        dataMap.dataMap.putString("message", message)
        val request = dataMap.asPutDataRequest()
        request.setUrgent()
        val dataItemTask: Task<DataItem> = Wearable.getDataClient(this).putDataItem(request)
        dataItemTask
            .addOnSuccessListener(OnSuccessListener<DataItem> { dataItem ->
                Log.d(
                    TAG,
                    "Sending message was successful: $dataItem"
                )
            })
            .addOnFailureListener(OnFailureListener { e ->
                Log.e(
                    TAG,
                    "Sending message failed: $e"
                )
            })
    }


}

