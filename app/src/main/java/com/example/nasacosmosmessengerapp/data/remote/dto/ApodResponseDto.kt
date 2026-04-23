package com.example.nasacosmosmessengerapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApodResponseDto(
    @SerializedName("date")
    val date: String,
    @SerializedName("explanation")
    val explanation: String,
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("hdurl")
    val hdUrl: String? = null,
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String? = null,
    @SerializedName("copyright")
    val copyright: String? = null
)
