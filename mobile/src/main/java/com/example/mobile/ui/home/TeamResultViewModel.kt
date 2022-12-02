package com.example.mobile.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.domain.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamResultViewModel(private val repository: MatchRepository):ViewModel() {
    private val _heatMapUrl = MutableStateFlow<String>("")
    val heatMapUrl = _heatMapUrl.asStateFlow()

    private val _replayUrl =  MutableStateFlow("")
    val replayUrl = _replayUrl.asStateFlow()

    fun loadHeatMap(){
        viewModelScope.launch {
            _heatMapUrl.value = repository.loadTeamHeatMap()
        }
    }

    fun loadReplay(){
        viewModelScope.launch {
            _replayUrl.value = repository.loadTeamReplay()
        }
    }
}
