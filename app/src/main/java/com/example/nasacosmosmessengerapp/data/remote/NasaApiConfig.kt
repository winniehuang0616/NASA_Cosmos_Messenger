package com.example.nasacosmosmessengerapp.data.remote

import com.example.nasacosmosmessengerapp.BuildConfig

object NasaApiConfig {
    val apiKey: String
        get() = BuildConfig.NASA_API_KEY
}
