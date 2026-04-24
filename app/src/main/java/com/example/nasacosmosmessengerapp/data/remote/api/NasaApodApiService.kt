package com.example.nasacosmosmessengerapp.data.remote.api

import com.example.nasacosmosmessengerapp.data.remote.dto.ApodResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApodApiService {

    @GET("planetary/apod")
    suspend fun getTodayApod(
        @Query("api_key") apiKey: String,
        @Query("thumbs") thumbs: Boolean = true
    ): ApodResponseDto

    @GET("planetary/apod")
    suspend fun getApodByDate(
        @Query("date") date: String,
        @Query("api_key") apiKey: String,
        @Query("thumbs") thumbs: Boolean = true
    ): ApodResponseDto
}
