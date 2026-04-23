package com.example.nasacosmosmessengerapp.data.remote

import com.example.nasacosmosmessengerapp.data.remote.api.NasaApodApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NasaApiModule {

    private const val BASE_URL = "https://api.nasa.gov/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val nasaApodApiService: NasaApodApiService by lazy {
        retrofit.create(NasaApodApiService::class.java)
    }
}
