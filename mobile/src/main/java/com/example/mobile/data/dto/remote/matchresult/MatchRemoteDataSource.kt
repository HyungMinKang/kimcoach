package com.example.mobile.data.dto.remote.matchresult

class MatchRemoteDataSource(private val api: MatchApi):MatchDataSource {
    override suspend fun loadPersonalHeatMap(): String {
        return api.getPersonalHeatMap()
    }

    override suspend fun loadTeamHeatMap(): String {
        return api.getTeamHeatMap()
    }

    override suspend fun loadPersonalReplay(): String {
        return api.getPersonalReplay()
    }

    override suspend fun loadTeamReplay(): String {
        return api.getTeamReplay()
    }

}