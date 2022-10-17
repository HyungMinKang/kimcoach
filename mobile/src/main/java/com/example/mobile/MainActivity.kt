package com.example.mobile

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val CHANNEL_MSG = "com.example.android.wearable.datalayer.channelmessage"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textview = findViewById<TextView>(R.id.test)
        textview.setOnClickListener {
            registerChannel()
        }


    }

    private fun registerChannel() {
        Toast.makeText(this, "CSV 파일 다운로드 시작", Toast.LENGTH_LONG).show()
         Wearable.getChannelClient(this)
            .registerChannelCallback(object : ChannelClient.ChannelCallback() {

                override fun onChannelOpened(channel: ChannelClient.Channel) {
                    super.onChannelOpened(channel)
                    println("channel opened")

                        if (channel.path == CHANNEL_MSG) {
                            val accFile = File("/acc.csv")
                            val gyroFile = File("/gyro.csv")
                            val gpsFile = File("/gps.csv")
                            val heartFile = File("/heart.csv")

                            try {
                                if (accFile.exists()) {
                                    accFile.delete()
                                }
                                if (gyroFile.exists()) {
                                    gyroFile.delete()
                                }
                                if (gpsFile.exists()) {
                                    gpsFile.delete()
                                }
                                if (heartFile.exists()) {
                                    heartFile.delete()
                                }

                                accFile.createNewFile()
                                gyroFile.createNewFile()
                                gpsFile.createNewFile()
                                heartFile.createNewFile()
                            } catch (e: IOException) {

                            }
                            Wearable.getChannelClient(applicationContext)
                                .receiveFile(channel, Uri.fromFile(accFile), false)
                            Wearable.getChannelClient(applicationContext)
                                .receiveFile(channel, Uri.fromFile(gyroFile), false)
                            Wearable.getChannelClient(applicationContext)
                                .receiveFile(channel, Uri.fromFile(gpsFile), false)
                            Wearable.getChannelClient(applicationContext)
                                .receiveFile(channel, Uri.fromFile(heartFile), false)
                        }

                }

                override fun onInputClosed(channel: ChannelClient.Channel, p1: Int, p2: Int) {
                    super.onInputClosed(channel, p1, p2)
                    Toast.makeText(this@MainActivity, "CSV 파일 다운로드 완료", Toast.LENGTH_LONG).show()
                    Wearable.getChannelClient(applicationContext).close(channel)
                }
            })

    }
}




