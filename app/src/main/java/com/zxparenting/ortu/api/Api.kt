package com.zxparenting.ortu.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

// ===== Model =====
data class LoginReq(val email: String, val password: String)
data class LoginRes(val token: String, val user: UserRes)
data class UserRes(val id: String, val nama: String, val role: String)

data class Device(
    val id: String,
    val deviceToken: String?,
    val nama: String,
    val sessionLimit: Int?,
    val gpsAktif: Boolean?,
    val latNow: Double?,
    val lngNow: Double?,
    val geoAktif: Boolean?,
    val geoLat: Double?,
    val geoLng: Double?,
    val geoRadius: Int?,
    val appBlokir: List<String>?,
    val anak: AnakRef?,
)

data class AnakRef(val id: String, val nama: String)

data class AktivitasRes(
    val logs: List<ActivityLog>,
    val ringkasan: Map<String, Int>?,
)

data class ActivityLog(
    val id: String,
    val tipe: String,
    val detail: String?,
    val createdAt: String,
)

// ===== API =====
interface ApiZx {
    @POST("auth/native-login")
    suspend fun login(@Body req: LoginReq): Response<LoginRes>

    @GET("device-session")
    suspend fun deviceList(@Header("Authorization") bearer: String): Response<List<Device>>

    @GET("aktivitas")
    suspend fun aktivitas(
        @Header("Authorization") bearer: String,
        @Query("anakId") anakId: String,
        @Query("limit") limit: Int = 50,
    ): Response<AktivitasRes>
}
