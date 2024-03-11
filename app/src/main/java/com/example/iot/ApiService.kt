package com.example.iot

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/predict")
    fun predictCrop(@Body data: Map<String, String>): Call<CropResponse>
}
