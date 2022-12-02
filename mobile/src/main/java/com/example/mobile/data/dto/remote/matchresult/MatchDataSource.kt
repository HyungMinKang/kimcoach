package com.example.mobile.data.dto.remote.matchresult

interface MatchDataSource{
    suspend fun loadPersonalHeatMap():String
    suspend fun loadTeamHeatMap():String
    suspend fun loadPersonalReplay():String
    suspend fun loadTeamReplay():String
}