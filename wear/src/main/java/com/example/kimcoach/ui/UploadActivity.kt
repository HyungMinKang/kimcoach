package com.example.kimcoach.ui

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.view.isVisible
import com.example.kimcoach.common.Constants
import com.example.kimcoach.common.Constants.CHANNEL_MSG
import com.example.kimcoach.databinding.ActivityUploadBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutionException

class UploadActivity : Activity() {

    private lateinit var binding: ActivityUploadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerSendDataBtn()
        registerMoveToHome()
    }

    private fun registerSendDataBtn() {
        binding.btnUploadSensor.setOnClickListener {
            sendCsvToMobile()
        }
    }

    private fun moveToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun getNodes(): Collection<String> {
        val results: HashSet<String> = HashSet()
        val nodeListTask: Task<List<Node>> = Wearable.getNodeClient(applicationContext).connectedNodes
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
       showLoadingProgress()

        CoroutineScope(Dispatchers.Default).launch {
            val nodes = getNodes()
            println(nodes)
            for (node in nodes) {
                val channelTask: Task<ChannelClient.Channel> = Wearable.getChannelClient(applicationContext).openChannel(node, CHANNEL_MSG)
                channelTask.addOnSuccessListener { channel ->
                    val accFile = File(applicationContext.getFileStreamPath(Constants.SENSOR_FILE_NAME).path)
                    val accFileUri = Uri.fromFile(accFile)
                    val uploadWork = Wearable.getChannelClient(applicationContext).sendFile(channel, accFileUri)
                    uploadWork.addOnSuccessListener {
                        showLoadingComplete()
                    }

                }
            }
        }
    }

    private fun showLoadingProgress() {
        binding.btnUploadSensor.isVisible = false
        binding.tvUploadLoading.isVisible = true
    }


    private fun showLoadingComplete() {
        Toast.makeText(this, "업로드 성공", LENGTH_SHORT).show()
        binding.pgUpload.isVisible = false
        binding.tvUploadLoading.isVisible = false
        binding.btnMoveToHome.isVisible = true
        binding.btnMoveToHome.isEnabled = true

    }

    private fun registerMoveToHome() {
        binding.btnMoveToHome.setOnClickListener {
            it.isEnabled = false
            moveToHome()
        }
    }

}