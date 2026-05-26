package com.mindshift.mobile.api

import com.mindshift.mobile.models.AnxietyPayload

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AnxietyApiService {

    @POST("anxiety-events")
    suspend fun sendAnxietyData(
        @Body payload: AnxietyPayload
    ): Response<Unit>

    @GET("anxiety-events/{user}")
    suspend fun getAnxietyData(
        @Path("user") user: String
    ): List<AnxietyPayload>
}