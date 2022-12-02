package com.example.mobile.data.dto.remote.matchresult

import retrofit2.http.GET

interface MatchApi {

    @GET
    suspend fun getPersonalHeatMap():String

    @GET
    suspend fun getTeamHeatMap():String

    @GET
    suspend fun getPersonalReplay():String

    @GET
    suspend fun getTeamReplay():String

}