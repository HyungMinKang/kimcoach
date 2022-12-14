package com.example.mobile.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
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
    private lateinit var navigator: NavController
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
        navigator = Navigation.findNavController(view)
        binding.rvHomeReservedMatch.adapter = adapter
        adapter.submitList(makeDummyMatchList())

        val completedMatchAdapter =
            HomeCompleteMatchAdapter({ item: CompletedMatch -> registerChannel() })
            { item: CompletedMatch ->
                moveToResultPage()
            }

        //fileTest()

        binding.fabHome.setOnClickListener {
            navigator.navigate(R.id.action_navigation_home_to_registerMatchFragment)
        }
        binding.rvHomeCompletedMatch.adapter = completedMatchAdapter
        completedMatchAdapter.submitList(makeDummyCompleteList())
    }

    private fun makeDummyMatchList(): List<ReservedMatch> {
        return listOf<ReservedMatch>(
            ReservedMatch("2022-12-25", "??????"),
            ReservedMatch("2022-12-03", "??????"),
            ReservedMatch("2022-12-15", "??????"),
            ReservedMatch("2022-12-18", "?????????"),
            ReservedMatch("2022-12-29", "??????")
        )
    }

    private fun makeDummyCompleteList(): MutableList<CompletedMatch> {
        val items = mutableListOf<CompletedMatch>()
        items.add(CompletedMatch("?????????", "2022-11-20", false))
        items.add(CompletedMatch("?????????", "2022-11-27", true))
        return items

    }


    private fun fileTest() {
        val file = File(requireActivity().applicationContext.getFileStreamPath("new.csv").path)
        viewModel.sendCsvToServer(file)
    }

    private fun moveToResultPage() {
        navigator.navigate(R.id.action_navigation_home_to_matchResultFragment)
    }

    private fun registerChannel() {
        Toast.makeText(requireContext(), "CSV ?????? ???????????? ??????", Toast.LENGTH_LONG).show()
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
                                        "CSV ?????? ???????????? ??????",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()

                                    val sensorFile = File(
                                        requireActivity().applicationContext.getFileStreamPath("SensorData.csv").path
                                    )
                                    println(sensorFile.isFile)
                                    println(sensorFile.totalSpace)
                                    viewModel.sendCsvToServer(sensorFile)
                                    Wearable.getChannelClient(requireActivity().applicationContext)
                                        .close(channel)
                                }
                            })

                    }


                })
        }
    }

}