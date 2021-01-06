package com.sch.simulatelocation.network

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Sch.
 * Date: 2020/12/11
 * description:
 */
interface ApiService {
    @GET("geocode/regeo?parameters")
    suspend fun getLocationInfo(
        @Query("key") key: String,
        @Query("location") latlon: String
    ):Result
}