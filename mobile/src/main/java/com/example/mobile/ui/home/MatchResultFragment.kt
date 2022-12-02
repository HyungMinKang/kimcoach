package com.example.mobile.ui.home

import android.graphics.Color
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import coil.api.load
import com.example.mobile.R
import com.example.mobile.common.Constants
import com.example.mobile.databinding.FragmentMatchResultBinding
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MatchResultFragment : Fragment() {

    private lateinit var binding: FragmentMatchResultBinding
    private lateinit var navigator: NavController
    private val viewModel: MatchResultViewModel by inject()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_match_result, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRadarChart()
        navigator = Navigation.findNavController(view)
        //video view에 video uri 설정 코드 추가 필요
        loadMatchVideo("test")
        loadHeatMap()
        registerHomeBtn()
        registerMoveToTeamResultBtn()

    }


    private fun loadHeatMap(){
        binding.ivMatchResultHeatMap.load("http://163.239.223.177:5006/images/heatmap10.png")
    }

    private fun registerHomeBtn(){
        binding.btnMatchResultBackHome.setOnClickListener {
            navigator.navigate(R.id.action_matchResultFragment_to_navigation_home)
        }
    }

    private fun registerMoveToTeamResultBtn(){
        binding.btnMatchResultMoveToTeamResult.setOnClickListener {
            navigator.navigate(R.id.action_matchResultFragment_to_teamResultFragment)
        }
    }

    private fun loadMatchVideo(url:String){


        binding.vvMatchResult.setVideoURI(Uri.parse("http://163.239.223.177:5006/vids/player.mp4"))


        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.vvMatchResult)

        binding.vvMatchResult.setOnPreparedListener(OnPreparedListener {
            //비디오 시작
            binding.vvMatchResult.setMediaController(mediaController)
            mediaController.setAnchorView(binding.vvMatchResult)
            binding.pgMatchResultVideo.isVisible = false
            binding.vvMatchResult.start()
        })
    }

    private fun initRadarChart() {
        val radarChart = binding.radarChartMatchResult
        val radaData = RadarData()
        radarChart.description.isEnabled = false
        radarChart.setBackgroundColor(Color.WHITE)
        radaData.addDataSet(getAverageDataSet())
        radaData.addDataSet(getPlayerDataSet())
        val labels = listOf("킥", "패스", "스피드", "뛴거리", "뛴시간", "커버리지 영역")
        val xAxis = radarChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        radarChart.data = radaData
    }

    private fun getAverageDataSet(): RadarDataSet {
        val radarDataSet = RadarDataSet(makeDataValue(), "Average")
        radarDataSet.color = Color.rgb(255, 100, 100)
        radarDataSet.fillColor = Color.rgb(255, 100, 100)
        radarDataSet.setDrawFilled(/* filled = */ true)
        radarDataSet.fillAlpha = 180
        radarDataSet.lineWidth = 2f
        radarDataSet.isDrawHighlightCircleEnabled = true
        radarDataSet.setDrawHighlightIndicators(/* enabled = */ false)
        return radarDataSet
    }

    private fun getPlayerDataSet(): RadarDataSet {
        val userRadarDataSet = RadarDataSet(makeDummyDataValue(), "Player")
        userRadarDataSet.color = Color.rgb(129, 198, 232);
        userRadarDataSet.fillColor = Color.rgb(129, 198, 232);
        userRadarDataSet.setDrawFilled(true);
        userRadarDataSet.fillAlpha = 180;
        userRadarDataSet.lineWidth = 2f;
        userRadarDataSet.isDrawHighlightCircleEnabled = true;
        userRadarDataSet.setDrawHighlightIndicators(false)
        return userRadarDataSet
    }

    private fun makeDataValue(): MutableList<RadarEntry> {

        val dataValues = mutableListOf<RadarEntry>()
        dataValues.add(RadarEntry(Constants.MAX_KICK))
        dataValues.add(RadarEntry(Constants.MAX_PASS))
        dataValues.add(RadarEntry(Constants.MAX_SPEED))
        dataValues.add(RadarEntry(Constants.MAX_RUN))
        dataValues.add(RadarEntry(Constants.MAX_TIME))
        dataValues.add(RadarEntry(Constants.MAX_COVERAGE))
        return dataValues
    }

    private fun makeDummyDataValue(): MutableList<RadarEntry> {
        val dataValues = mutableListOf<RadarEntry>()
        dataValues.add(RadarEntry(80F))
        dataValues.add(RadarEntry(80F))
        dataValues.add(RadarEntry(75F))
        dataValues.add(RadarEntry(70F))
        dataValues.add(RadarEntry(60F))
        dataValues.add(RadarEntry(40f))
        return dataValues
    }

    override fun onStop() {
        super.onStop()
        if( binding.vvMatchResult.isPlaying){
            binding.vvMatchResult.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.vvMatchResult.stopPlayback()
    }
}