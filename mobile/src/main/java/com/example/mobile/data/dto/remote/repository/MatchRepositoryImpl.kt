package com.example.mobile.data.dto.remote.repository

import com.example.mobile.data.dto.remote.matchresult.MatchDataSource
import com.example.mobile.domain.MatchRepository

class MatchRepositoryImpl(private val dataSource: MatchDataSource):
    MatchRepository {
    override suspend fun loadPersonalHeatMap(): String {
        return dataSource.loadPersonalHeatMap()
    }

    override suspend fun loadTeamHeatMap(): String {
        return dataSource.loadTeamHeatMap()
    }

    override suspend fun loadPersonalReplay(): String {
        return dataSource.loadPersonalReplay()
    }

    override suspend fun loadTeamReplay(): String {
        return dataSource.loadTeamReplay()
    }

}