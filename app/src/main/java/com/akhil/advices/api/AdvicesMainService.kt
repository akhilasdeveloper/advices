package com.akhil.advices.api

import retrofit2.http.GET

interface AdvicesMainService {
    @GET("advice")
    suspend fun getAdvice(): AdviceResponse?
}