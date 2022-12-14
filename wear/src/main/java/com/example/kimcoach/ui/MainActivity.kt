package com.example.kimcoach.ui

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.kimcoach.R
import com.example.kimcoach.databinding.ActivityMainBinding


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
            moveToSensorMatch()
        }
    }

    private fun moveToSensorMatch(){
        val intent = Intent(this, SensorActivity::class.java  )
        startActivity(intent)
    }

    private fun isAllGranted(): Boolean = REQUIRED_PERMISSIONS.all { permission ->
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermission() {
        if (isAllGranted()) {
            println(getString(R.string.permission_granted_message))
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ?????? ?????? ?????? ??????
            } else {
                if (shouldShowRequestPermissionRationale(REQUIRED_PERMISSIONS[0])) {
                    //?????? ????????? ?????? ????????? ????????? ??????????????? ??????
                    requestPermission()
                } else {
                    // ?????? ????????? ?????????
                }
            }
        }
    }
}






