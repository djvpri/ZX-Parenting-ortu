package com.zxparenting.ortu.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Klien {
    private const val BASE = "https://zxparenting.zomet.my.id/api/"

    private val ok = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: ApiZx by lazy {
        Retrofit.Builder()
            .baseUrl(BASE)
            .client(ok)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiZx::class.java)
    }
}
