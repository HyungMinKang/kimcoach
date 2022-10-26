package com.example.kimcoach.ui

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.kimcoach.R
import com.example.kimcoach.common.Constants
import com.example.kimcoach.databinding.ActivityUploadBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutionException

class UploadActivity: Activity() {

    private val CHANNEL_MSG = "com.example.android.wearable.datalayer.channelmessage"
    private lateinit var binding: ActivityUploadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerSendDataBtn()
    }


    private fun registerSendDataBtn() {
        binding.btnUploadSensor.setOnClickListener {
            sendCsvToMobile()
            moveToHome()
//            val snackBar = Snackbar.make(binding.layout, "업로드 완료" , Snackbar.LENGTH_LONG)
//            snackBar.setAction("확인", View.OnClickListener {
//                moveToHome()
//            })
//            snackBar.show()
        }
    }
    private fun moveToHome(){
        val intent = Intent(this, MainActivity::class.java )
        startActivity(intent)
    }

    private fun getNodes(): Collection<String> {
        val results: HashSet<String> = HashSet()
        val nodeListTask: Task<List<Node>> = Wearable.getNodeClient(
            applicationContext
        ).connectedNodes
        try {
            val nodes: List<Node> = Tasks.await(nodeListTask)
            for (node in nodes) {
                results.add(node.id)
            }

        } catch (exception: ExecutionException) {
            Log.e(ContentValues.TAG, "Task failed: $exception")
        } catch (exception: InterruptedException) {
            Log.e(ContentValues.TAG, "Interrupt occurred: $exception")
        }
        return results
    }

    private fun sendCsvToMobile() {
        CoroutineScope(Dispatchers.IO).launch {
            val nodes = getNodes()
            for (node in nodes) {
                val channelTask: Task<ChannelClient.Channel> = Wearable.getChannelClient(applicationContext).openChannel(node, CHANNEL_MSG)
                channelTask.addOnSuccessListener { channel ->
                    val accFile = File(applicationContext.getFileStreamPath(Constants.SENSOR_FILE_NAME).path)
                    val accFileUri = Uri.fromFile(accFile)


                    Wearable.getChannelClient(applicationContext)
                        .sendFile(channel, accFileUri)

                }
            }
        }


    }

}