package com.example.mobile.ui.home

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import coil.api.load
import com.example.mobile.R
import com.example.mobile.databinding.FragmentTeamResultBinding
import org.koin.android.ext.android.inject


class TeamResultFragment : Fragment() {

    private lateinit var binding:FragmentTeamResultBinding
    private lateinit var navigator:NavController
    private val viewModel : TeamResultViewModel by inject()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_team_result, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadMatchVideo("test")
        navigator = Navigation.findNavController(view)
        registerHomeBtn()
        loadHeatMap()
        registerMoveToPersonResultBtn()
    }


    private fun loadHeatMap(){
        binding.ivMatchResultHeatMap.load("http://163.239.223.177:5006/images/heatmap_team.png")
    }

    private fun registerHomeBtn(){
        binding.btnMatchResultBackHome.setOnClickListener {
            navigator.navigate(R.id.action_teamResultFragment_to_navigation_home)
        }
    }

    private fun registerMoveToPersonResultBtn(){
        binding.btnMatchResultMoveToTeamResult.setOnClickListener {
            navigator.navigate(R.id.action_teamResultFragment_to_matchResultFragment)
        }
    }

    private fun loadMatchVideo(url:String){
        binding.vvMatchResult.setVideoURI(Uri.parse("http://163.239.223.177:5006/vids/team.mp4"))
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.vvMatchResult)

        binding.vvMatchResult.setOnPreparedListener(MediaPlayer.OnPreparedListener {
            //????????? ??????
            binding.vvMatchResult.setMediaController(mediaController)
            mediaController.setAnchorView(binding.vvMatchResult)
            binding.pgMatchResultVideo.isVisible = false
            binding.vvMatchResult.start()
        })
    }

}