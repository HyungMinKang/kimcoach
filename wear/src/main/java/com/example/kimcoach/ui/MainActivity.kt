package com.example.kimcoach.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.example.kimcoach.common.Constants
import com.example.kimcoach.databinding.ActivityMainBinding
import com.example.kimcoach.room.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.ExecutionException


class MainActivity : Activity(){

    private val REQUEST_CODE_PERMISSION = 1001
    private lateinit var binding: ActivityMainBinding
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BODY_SENSORS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermission()
        registerStartBtn()
    }

    private fun registerStartBtn() {
        binding.btnStartSensor.setOnClickListener {
            binding.btnStartSensor.isVisible = false
            binding.pgMain.isVisible = true
            binding.tvMainLoading.isVisible = true
            val intent = Intent(this, SensorActivity::class.java  )
            startActivity(intent)
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
}






