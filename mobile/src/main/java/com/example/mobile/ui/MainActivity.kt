package com.example.mobile.ui

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mobile.R
import com.example.mobile.databinding.ActivityMainBinding
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        setupNav()
    }

    private fun setupNav() {
        val navController = supportFragmentManager.findFragmentById(R.id.fragment_container)?.findNavController()
        navController?.let {
            binding.bottomNavigationView.setupWithNavController(it)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> hideBottomNavigation()
                else -> showBottomNavigation()
            }
        }
    }

    private fun showBottomNavigation(){
        binding.bottomNavigationView.isVisible = true
    }

    private fun hideBottomNavigation(){
        binding.bottomNavigationView.isVisible = false
    }


    private fun registerChannel() {
        Toast.makeText(this, "CSV 파일 다운로드 시작", Toast.LENGTH_LONG).show()
        CoroutineScope(Dispatchers.IO).launch {
            Wearable.getChannelClient(applicationContext)
                .registerChannelCallback(object : ChannelClient.ChannelCallback() {
                    override fun onChannelOpened(channel: ChannelClient.Channel) {
                        super.onChannelOpened(channel)
                        println("channel opened")


                        val sensorFile =
                            File(applicationContext.getFileStreamPath("SensorData.csv").path)

                        if (sensorFile.exists()) {
                            sensorFile.delete()
                        }
                        Wearable.getChannelClient(applicationContext)
                            .receiveFile(channel, Uri.fromFile(sensorFile), true)
                        Wearable.getChannelClient(applicationContext)
                            .registerChannelCallback(object : ChannelClient.ChannelCallback() {
                                override fun onInputClosed(
                                    channel: ChannelClient.Channel,
                                    p1: Int,
                                    p2: Int
                                ) {
                                    super.onInputClosed(channel, p1, p2)
                                    Toast.makeText(
                                        this@MainActivity,
                                        "CSV 파일 다운로드 완료",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    Wearable.getChannelClient(applicationContext).close(channel)
                                }
                            })

                    }


                })
        }
    }
}




