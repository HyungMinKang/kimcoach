package com.example.mobile.domain

interface MatchRepository {
    suspend fun loadPersonalHeatMap():String
    suspend fun loadTeamHeatMap():String
    suspend fun loadPersonalReplay():String
    suspend fun loadTeamReplay():String
}