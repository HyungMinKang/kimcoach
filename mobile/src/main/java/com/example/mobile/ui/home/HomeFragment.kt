package com.example.mobile.ui.home

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.databinding.FragmentHomeBinding
import com.example.mobile.domain.model.CompletedMatch
import com.example.mobile.domain.model.ReservedMatch
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by inject()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = HomeAdapter()
        binding.rvHomeReservedMatch.adapter = adapter
        adapter.submitList(makeDummyMatchList())

        val completedMatchAdapter = HomeCompleteMatchAdapter {
            item: CompletedMatch ->  registerChannel()
        }
        //fileTest()
        binding.rvHomeCompletedMatch.adapter = completedMatchAdapter
        completedMatchAdapter.submitList(makeDummyCompleteList())
    }

    private fun makeDummyMatchList(): List<ReservedMatch> {
        return listOf<ReservedMatch>(ReservedMatch("2022-10-31","수원"),ReservedMatch("2022-10-31","인천"),ReservedMatch("2022-11-15","안양"), ReservedMatch("2022-11-31","의정부") )
    }

    private fun makeDummyCompleteList(): MutableList<CompletedMatch> {
        val items = mutableListOf<CompletedMatch>()
        items.add(CompletedMatch("XXX", "XXXX-XX-XX", false))
        items.add(CompletedMatch("XXX", "XXXX-XX-XX", false))
        items.add(CompletedMatch("XXX", "XXXX-XX-XX", false))
        items.add(CompletedMatch("XXX", "XXXX-XX-XX", true))
        items.add(CompletedMatch("XXX", "XXXX-XX-XX", true))
        return items

    }


    private fun fileTest(){
        val file=  File(requireActivity().applicationContext.getFileStreamPath("new.csv").path)
        viewModel.sendCsvToServer(file)
    }
    private fun registerChannel() {
        Toast.makeText(requireContext(), "CSV 파일 다운로드 시작", Toast.LENGTH_LONG).show()
        CoroutineScope(Dispatchers.IO).launch {
            Wearable.getChannelClient(requireActivity().applicationContext)
                .registerChannelCallback(object : ChannelClient.ChannelCallback() {
                    override fun onChannelOpened(channel: ChannelClient.Channel) {
                        super.onChannelOpened(channel)
                        println("channel opened")


                        val sensorFile =
                            File(requireActivity().applicationContext.getFileStreamPath("SensorData.csv").path)

                        if (sensorFile.exists()) {
                            sensorFile.delete()
                        }
                        Wearable.getChannelClient(requireActivity().applicationContext)
                            .receiveFile(channel, Uri.fromFile(sensorFile), true)
                        Wearable.getChannelClient(requireActivity().applicationContext)
                            .registerChannelCallback(object : ChannelClient.ChannelCallback() {
                                override fun onInputClosed(
                                    channel: ChannelClient.Channel,
                                    p1: Int,
                                    p2: Int
                                ) {
                                    super.onInputClosed(channel, p1, p2)
                                    Toast.makeText(
                                        requireContext(),
                                        "CSV 파일 다운로드 완료",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()

                                    val sensorFile = File(requireActivity().applicationContext.getFileStreamPath("SensorData.csv").path)
                                    println(sensorFile.isFile)
                                    println(sensorFile.totalSpace)
                                    viewModel.sendCsvToServer(sensorFile)
                                    Wearable.getChannelClient(requireActivity().applicationContext).close(channel)
                                }
                            })

                    }


                })
        }
    }

}