package com.example.mobile.ui.my

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.mobile.R
import com.example.mobile.common.Constants
import com.example.mobile.databinding.FragmentMyBinding
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class MyFragment : Fragment() {

    private lateinit var binding:FragmentMyBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRadarChart()
    }

    private fun initRadarChart() {
        val radarChart = binding.radarChartMy
        val radaData = RadarData()
        radarChart.description.isEnabled = false
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
}