package com.zxparenting.ortu.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// ===== Auth =====
data class LoginReq(val email: String, val password: String)
data class LoginRes(val token: String, val user: UserRes)
data class UserRes(val id: String, val nama: String, val role: String)

data class DaftarReq(val nama: String, val email: String, val password: String, val setuju: Boolean)
data class GoogleReq(val idToken: String, val audience: String)

// ===== Anak =====
data class Anak(
    val id: String,
    val nama: String,
    val umur: Int?,
    @SerializedName("tokenBalance") val tokenBalance: TokenBalanceRes?,
    val dormant: Boolean = false,
)
data class TokenBalanceRes(val balance: Int = 0)

data class AnakCreateReq(
    val nama: String,
    val username: String,
    val pin: String,
    val umur: Int,
    val kelas: String? = null,
    val gender: String? = null,
    val agama: String? = null,
)

// ===== Tugas =====
data class Tugas(
    val id: String,
    val judul: String,
    val deskripsi: String?,
    val tokenReward: Int,
    val status: String,
    val fotoBuktiUrl: String?,
    val validasiOrtu: Boolean?,
    val deadline: String?,
    val createdAt: String,
    val anak: AnakRef,
)
data class TugasCreateReq(
    val anakId: String,
    val judul: String,
    val deskripsi: String? = null,
    val tokenReward: Int = 1,
    val deadline: String? = null,
)
data class TugasValidasiReq(val aksi: String) // "selesai" | "tolak"

// ===== Device =====
data class Device(
    val id: String,
    val deviceToken: String?,
    val nama: String,
    val sessionLimit: Int?,
    val cooldown: Int?,
    val dailyLimit: Int?,
    val curfewMulai: String?,
    val curfewSelesai: String?,
    val masterUnlock: Boolean?,
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

data class DevicePatch(
    val sessionLimit: Int? = null,
    val cooldown: Int? = null,
    val dailyLimit: Int? = null,
    val curfewMulai: String? = null,
    val curfewSelesai: String? = null,
    val masterUnlock: Boolean? = null,
    val gpsAktif: Boolean? = null,
    val geoAktif: Boolean? = null,
    val appBlokir: List<String>? = null,
)

// ===== Aktivitas =====
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

    @POST("auth/native-daftar")
    suspend fun daftar(@Body req: DaftarReq): Response<LoginRes>

    @POST("auth/native-google")
    suspend fun loginGoogle(@Body req: GoogleReq): Response<LoginRes>

    // Anak
    @GET("anak")
    suspend fun anakList(@Header("Authorization") bearer: String): Response<List<Anak>>

    @POST("anak")
    suspend fun anakCreate(
        @Header("Authorization") bearer: String,
        @Body req: AnakCreateReq,
    ): Response<AnakCreateRes>

    // Tugas
    @GET("tugas")
    suspend fun tugasList(@Header("Authorization") bearer: String): Response<List<Tugas>>

    @POST("tugas")
    suspend fun tugasCreate(
        @Header("Authorization") bearer: String,
        @Body req: TugasCreateReq,
    ): Response<TugasCreateRes>

    @PATCH("tugas/{id}")
    suspend fun tugasValidasi(
        @Header("Authorization") bearer: String,
        @Path("id") id: String,
        @Body req: TugasValidasiReq,
    ): Response<TugasOkRes>

    // Device
    @GET("device-session")
    suspend fun deviceList(@Header("Authorization") bearer: String): Response<List<Device>>

    @PATCH("device-session/{id}")
    suspend fun devicePatch(
        @Header("Authorization") bearer: String,
        @Path("id") id: String,
        @Body req: DevicePatch,
    ): Response<Device>

    @DELETE("device-session/{id}")
    suspend fun deviceDelete(
        @Header("Authorization") bearer: String,
        @Path("id") id: String,
    ): Response<TugasOkRes>

    // Akun
    @DELETE("akun/hapus")
    suspend fun akunHapus(@Header("Authorization") bearer: String): Response<TugasOkRes>

    // Aktivitas
    @GET("aktivitas")
    suspend fun aktivitas(
        @Header("Authorization") bearer: String,
        @Query("anakId") anakId: String,
        @Query("limit") limit: Int = 50,
    ): Response<AktivitasRes>
}

data class AnakCreateRes(val ok: Boolean, val anakId: String)
data class TugasCreateRes(val ok: Boolean, val tugasId: String)
data class TugasOkRes(val ok: Boolean)
