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
    val geoLat: Double? = null,
    val geoLng: Double? = null,
    val geoRadius: Int? = null,
    val appBlokir: List<String>? = null,
)

// ===== Hadiah / Marketplace =====
data class Hadiah(
    val id: String,
    val judul: String,
    val deskripsi: String?,
    val hargaCoin: Int,
    val stok: Int,
    val aktif: Boolean,
)
data class HadiahCreateReq(
    val judul: String,
    val deskripsi: String? = null,
    val hargaCoin: Int,
    val stok: Int = 1,
)
data class PesananHadiah(
    val id: String,
    val status: String,
    val createdAt: String,
    val anak: AnakRef,
    val hadiah: HadiahRef,
)
data class HadiahRef(val judul: String, val hargaCoin: Int)
data class HadiahMarketRes(val hadiah: List<Hadiah>, val pesanan: List<PesananHadiah>)
data class PesananProsesReq(val id: String, val aksi: String) // "beli" | "tolak"

// ===== Coin =====
data class CoinRes(val saldo: Int, val ledger: List<CoinLedgerEntry>)
data class CoinLedgerEntry(
    val id: String,
    val tipe: String,
    val jumlah: Int,
    val catatan: String?,
    val createdAt: String,
)
data class CoinTopupReq(val aksi: String, val jumlah: Int)

// ===== Family Quest =====
data class Quest(
    val id: String,
    val judul: String,
    val deskripsi: String?,
    val tokenReward: Int,
    val deadline: String?,
    val anggota: List<QuestAnggota>,
)
data class QuestAnggota(val anakId: String, val selesai: Boolean, val anak: AnakRef?)
data class QuestCreateReq(
    val judul: String,
    val deskripsi: String? = null,
    val tokenReward: Int,
    val deadline: String? = null,
)

// ===== Forum =====
data class ForumPost(
    val id: String,
    val judul: String,
    val isi: String,
    val kategori: String,
    val createdAt: String,
    val ortu: AnakRef, // reuse: nama field
    val _count: KomentarCount? = null,
    val komentar: List<ForumKomentar>? = null,
)
data class KomentarCount(val komentar: Int)
data class ForumKomentar(
    val id: String,
    val isi: String,
    val createdAt: String,
    val ortu: AnakRef,
)
data class ForumCreateReq(
    val judul: String,
    val isi: String,
    val kategori: String = "umum",
)
data class KomentarReq(val isi: String)

// ===== Aktivitas =====
data class AktivitasRes(
    val anak: AnakRef,
    val logs: List<ActivityLog>,
    val ringkasan7hari: List<RingkasTipe>,
)
data class ActivityLog(
    val id: String,
    val tipe: String,
    val detail: String?,
    val createdAt: String,
)
data class RingkasTipe(val tipe: String, val jumlah: Int)

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

    // Hadiah / Marketplace
    @GET("hadiah")
    suspend fun hadiahList(@Header("Authorization") bearer: String): Response<HadiahMarketRes>

    @POST("hadiah")
    suspend fun hadiahCreate(
        @Header("Authorization") bearer: String,
        @Body req: HadiahCreateReq,
    ): Response<Hadiah>

    @PATCH("hadiah")
    suspend fun hadiahPatch(
        @Header("Authorization") bearer: String,
        @Body req: HadiahPatchReq,
    ): Response<Hadiah>

    @DELETE("hadiah")
    suspend fun hadiahDelete(
        @Header("Authorization") bearer: String,
        @Query("id") id: String,
    ): Response<TugasOkRes>

    @POST("hadiah/pesanan")
    suspend fun pesananProses(
        @Header("Authorization") bearer: String,
        @Body req: PesananProsesReq,
    ): Response<PesananProsesRes>

    // Coin
    @GET("coin")
    suspend fun coinGet(@Header("Authorization") bearer: String): Response<CoinRes>

    @POST("coin")
    suspend fun coinPost(
        @Header("Authorization") bearer: String,
        @Body req: CoinTopupReq,
    ): Response<CoinSaldoRes>

    // Quest
    @GET("quests")
    suspend fun questList(@Header("Authorization") bearer: String): Response<List<Quest>>

    @POST("quests")
    suspend fun questCreate(
        @Header("Authorization") bearer: String,
        @Body req: QuestCreateReq,
    ): Response<Quest>

    // Forum
    @GET("forum")
    suspend fun forumList(@Header("Authorization") bearer: String): Response<List<ForumPost>>

    @POST("forum")
    suspend fun forumCreate(
        @Header("Authorization") bearer: String,
        @Body req: ForumCreateReq,
    ): Response<ForumPost>

    @GET("forum/{id}")
    suspend fun forumDetail(
        @Header("Authorization") bearer: String,
        @Path("id") id: String,
    ): Response<ForumPost>

    @DELETE("forum/{id}")
    suspend fun forumDelete(
        @Header("Authorization") bearer: String,
        @Path("id") id: String,
    ): Response<TugasOkRes>

    @POST("forum/{id}/komentar")
    suspend fun forumKomentar(
        @Header("Authorization") bearer: String,
        @Path("id") id: String,
        @Body req: KomentarReq,
    ): Response<ForumKomentar>
}

data class AnakCreateRes(val ok: Boolean, val anakId: String)
data class TugasCreateRes(val ok: Boolean, val tugasId: String)
data class TugasOkRes(val ok: Boolean)
data class HadiahPatchReq(val id: String, val aktif: Boolean? = null, val stok: Int? = null, val hargaCoin: Int? = null)
data class PesananProsesRes(val ok: Boolean, val status: String, val saldo: Int? = null)
data class CoinSaldoRes(val saldo: Int)
