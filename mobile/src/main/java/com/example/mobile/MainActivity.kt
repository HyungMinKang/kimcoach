package com.example.mobile

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kimcoach.common.Constants
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutionException


class MainActivity : AppCompatActivity() {
    private val CHANNEL_MSG = "com.example.android.wearable.datalayer.channelmessage"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textview = findViewById<TextView>(R.id.test)
        textview.setOnClickListener {
            registerChannel()
        }
        val sensorFile = File(applicationContext.getFileStreamPath("SensorData.csv").path)


    }


    private fun registerChannel() {
        Toast.makeText(this, "CSV 파일 다운로드 시작", Toast.LENGTH_LONG).show()
        CoroutineScope(Dispatchers.IO).launch {
            Wearable.getChannelClient(applicationContext)
                .registerChannelCallback(object : ChannelClient.ChannelCallback() {
                    override fun onChannelOpened(channel: ChannelClient.Channel) {
                        super.onChannelOpened(channel)
                        println("channel opened")


                        val sensorFile = File(applicationContext.getFileStreamPath("SensorData.csv").path)

                        if(sensorFile.exists()){
                            sensorFile.delete()
                        }
                        Wearable.getChannelClient(applicationContext)
                            .receiveFile(channel, Uri.fromFile(sensorFile), true)
                        Wearable.getChannelClient(applicationContext).registerChannelCallback(object : ChannelClient.ChannelCallback(){
                            override fun onInputClosed(channel: ChannelClient.Channel, p1: Int, p2: Int) {
                                super.onInputClosed(channel, p1, p2)
                                Toast.makeText(this@MainActivity, "CSV 파일 다운로드 완료", Toast.LENGTH_LONG)
                                    .show()
                                Wearable.getChannelClient(applicationContext).close(channel)
                            }
                        })

                    }


                })
        }
    }
}




