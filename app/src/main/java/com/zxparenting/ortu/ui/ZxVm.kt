package com.zxparenting.ortu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zxparenting.ortu.api.AktivitasRes
import com.zxparenting.ortu.api.Anak
import com.zxparenting.ortu.api.AnakCreateReq
import com.zxparenting.ortu.api.ApiZx
import com.zxparenting.ortu.api.CoinRes
import com.zxparenting.ortu.api.CoinTopupReq
import com.zxparenting.ortu.api.Device
import com.zxparenting.ortu.api.DevicePatch
import com.zxparenting.ortu.api.DaftarReq
import com.zxparenting.ortu.api.ForumCreateReq
import com.zxparenting.ortu.api.ForumPost
import com.zxparenting.ortu.api.GoogleReq
import com.zxparenting.ortu.api.HadiahCreateReq
import com.zxparenting.ortu.api.HadiahMarketRes
import com.zxparenting.ortu.api.Klien
import com.zxparenting.ortu.api.KomentarReq
import com.zxparenting.ortu.api.LanggananRes
import com.zxparenting.ortu.api.PesanKirimReq
import com.zxparenting.ortu.api.PesanRingkas
import com.zxparenting.ortu.api.InsightsRes
import com.zxparenting.ortu.api.PinjamRes
import com.zxparenting.ortu.api.ReferralRes
import com.zxparenting.ortu.api.LeaderboardRes
import com.zxparenting.ortu.api.ChallengesRes
import com.zxparenting.ortu.api.ChallengeClaimReq
import com.zxparenting.ortu.api.ChallengeClaimRes
import com.zxparenting.ortu.api.ReferralClaimReq
import com.zxparenting.ortu.api.LoginReq
import com.zxparenting.ortu.api.LoginRes
import com.zxparenting.ortu.api.PesananProsesReq
import com.zxparenting.ortu.api.Pesan
import com.zxparenting.ortu.api.PesananHadiah
import com.zxparenting.ortu.api.Quest
import com.zxparenting.ortu.api.QuestCreateReq
import com.zxparenting.ortu.api.Tugas
import com.zxparenting.ortu.api.TugasCreateReq
import com.zxparenting.ortu.api.TugasValidasiReq
import com.zxparenting.ortu.data.Simpanan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class UiState(
    val loading: Boolean = false,
    val error: String? = null,
    val loginOk: Boolean = false,
    val nama: String? = null,
    val token: String? = null,
    val devices: List<Device> = emptyList(),
    val anakList: List<Anak> = emptyList(),
    val tugasList: List<Tugas> = emptyList(),
    val aktivitas: AktivitasRes? = null,
    val market: HadiahMarketRes? = null,
    val coin: CoinRes? = null,
    val quests: List<Quest> = emptyList(),
    val forumPosts: List<ForumPost> = emptyList(),
    val forumDetail: ForumPost? = null,
    val langganan: LanggananRes? = null,
    val pesanList: List<PesanRingkas> = emptyList(),
    val pesanThread: List<Pesan> = emptyList(),
    val insights: InsightsRes? = null,
    val pinjam: PinjamRes? = null,
    val referral: ReferralRes? = null,
    val leaderboard: LeaderboardRes? = null,
    val challenges: ChallengesRes? = null,
    val claimResult: String? = null,
)

class ZxVm(val simpanan: Simpanan) : ViewModel() {
    private val api: ApiZx = Klien.api
    val state = MutableStateFlow(UiState())
    val s: StateFlow<UiState> get() = state

    init {
        viewModelScope.launch {
            val t = simpanan.token.first()
            if (t != null) {
                state.value = state.value.copy(loginOk = true, nama = simpanan.nama.first(), token = t)
                muatSemua(t)
            }
        }
    }

    fun login(email: String, password: String) {
        state.value = state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val res = api.login(LoginReq(email, password))
                if (res.isSuccessful) {
                    suksesLogin(res.body()!!)
                } else {
                    state.value = state.value.copy(
                        loading = false,
                        error = "Email atau password salah",
                    )
                }
            } catch (e: Exception) {
                state.value = state.value.copy(loading = false, error = "Jaringan error: ${e.message}")
            }
        }
    }

    fun daftar(nama: String, email: String, password: String) {
        state.value = state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val res = api.daftar(DaftarReq(nama, email, password, setuju = true))
                if (res.isSuccessful) {
                    suksesLogin(res.body()!!)
                } else {
                    val msg = when (res.code()) {
                        409 -> "Email sudah terdaftar"
                        400 -> "Data tidak valid"
                        else -> "Gagal daftar (${res.code()})"
                    }
                    state.value = state.value.copy(loading = false, error = msg)
                }
            } catch (e: Exception) {
                state.value = state.value.copy(loading = false, error = "Jaringan error: ${e.message}")
            }
        }
    }

    fun loginGoogle(idToken: String, audience: String) {
        state.value = state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val res = api.loginGoogle(GoogleReq(idToken, audience))
                if (res.isSuccessful) {
                    suksesLogin(res.body()!!)
                } else {
                    state.value = state.value.copy(
                        loading = false,
                        error = "Login Google gagal (${res.code()})",
                    )
                }
            } catch (e: Exception) {
                state.value = state.value.copy(loading = false, error = "Jaringan error: ${e.message}")
            }
        }
    }

    fun setError(msg: String) {
        state.value = state.value.copy(loading = false, error = msg)
    }

    private suspend fun suksesLogin(body: LoginRes) {
        simpanan.simpanSesi(body.token, body.user.nama, body.user.id)
        state.value = state.value.copy(
            loading = false,
            loginOk = true,
            nama = body.user.nama,
            token = body.token,
        )
        muatSemua(body.token)
    }

    // Muat semua data awal: devices + anak + tugas + market + coin + quest + forum.
    fun muatSemua(token: String?) {
        if (token == null) return
        viewModelScope.launch {
            muatDevices(token)
            muatAnak(token)
            muatTugas(token)
            muatMarket(token)
            muatCoin(token)
            muatQuest(token)
            muatForum(token)
            muatLangganan(token)
        }
    }

    fun muatLangganan(token: String?) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.langgananStatus("Bearer $token")
                if (res.isSuccessful) {
                    state.value = state.value.copy(langganan = res.body())
                }
            } catch (_: Exception) {}
        }
    }

    fun muatDevices(token: String?) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.deviceList("Bearer $token")
                if (res.isSuccessful) {
                    state.value = state.value.copy(devices = res.body() ?: emptyList())
                }
            } catch (_: Exception) {}
        }
    }

    fun muatAnak(token: String?) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.anakList("Bearer $token")
                if (res.isSuccessful) {
                    state.value = state.value.copy(anakList = res.body() ?: emptyList())
                }
            } catch (_: Exception) {}
        }
    }

    fun muatTugas(token: String?) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.tugasList("Bearer $token")
                if (res.isSuccessful) {
                    state.value = state.value.copy(tugasList = res.body() ?: emptyList())
                }
            } catch (_: Exception) {}
        }
    }

    fun muatAktivitas(token: String?, anakId: String) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.aktivitas("Bearer $token", anakId)
                if (res.isSuccessful) {
                    state.value = state.value.copy(aktivitas = res.body())
                }
            } catch (_: Exception) {}
        }
    }

    // ===== F3: Marketplace / Coin / Quest =====

    fun muatMarket(token: String?) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.hadiahList("Bearer $token")
                if (res.isSuccessful) {
                    state.value = state.value.copy(market = res.body())
                }
            } catch (_: Exception) {}
        }
    }

    fun buatHadiah(judul: String, deskripsi: String?, hargaCoin: Int, stok: Int) {
        val token = state.value.token ?: return
        state.value = state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val res = api.hadiahCreate("Bearer $token", HadiahCreateReq(judul, deskripsi, hargaCoin, stok))
                state.value = state.value.copy(loading = false)
                if (res.isSuccessful) muatMarket(token)
                else state.value = state.value.copy(error = "Gagal buat hadiah (${res.code()})")
            } catch (e: Exception) {
                state.value = state.value.copy(loading = false, error = "Jaringan error: ${e.message}")
            }
        }
    }

    fun hapusHadiah(id: String) {
        val token = state.value.token ?: return
        viewModelScope.launch {
            try {
                api.hadiahDelete("Bearer $token", id)
                muatMarket(token)
            } catch (_: Exception) {}
        }
    }

    fun prosesPesanan(pesananId: String, aksi: String) {
        val token = state.value.token ?: return
        viewModelScope.launch {
            try {
                val res = api.pesananProses("Bearer $token", PesananProsesReq(pesananId, aksi))
                if (res.isSuccessful) {
                    muatMarket(token)
                    muatCoin(token) // saldo berubah kalau beli
                } else if (res.code() == 400) {
                    state.value = state.value.copy(error = "Saldo Coin tidak cukup")
                }
            } catch (_: Exception) {}
        }
    }

    fun muatCoin(token: String?) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.coinGet("Bearer $token")
                if (res.isSuccessful) {
                    state.value = state.value.copy(coin = res.body())
                }
            } catch (_: Exception) {}
        }
    }

    fun topupCoin(jumlah: Int) {
        val token = state.value.token ?: return
        state.value = state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val res = api.coinPost("Bearer $token", CoinTopupReq("topup", jumlah))
                state.value = state.value.copy(loading = false)
                if (res.isSuccessful) muatCoin(token)
                else state.value = state.value.copy(error = "Gagal topup (${res.code()})")
            } catch (e: Exception) {
                state.value = state.value.copy(loading = false, error = "Jaringan error: ${e.message}")
            }
        }
    }

    fun muatQuest(token: String?) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.questList("Bearer $token")
                if (res.isSuccessful) {
                    state.value = state.value.copy(quests = res.body() ?: emptyList())
                }
            } catch (_: Exception) {}
        }
    }

    fun buatQuest(judul: String, deskripsi: String?, tokenReward: Int, deadline: String? = null) {
        val token = state.value.token ?: return
        state.value = state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val res = api.questCreate("Bearer $token", QuestCreateReq(judul, deskripsi, tokenReward, deadline))
                state.value = state.value.copy(loading = false)
                if (res.isSuccessful) muatQuest(token)
                else state.value = state.value.copy(error = "Gagal buat quest (${res.code()})")
            } catch (e: Exception) {
                state.value = state.value.copy(loading = false, error = "Jaringan error: ${e.message}")
            }
        }
    }

    fun buatAnak(
        nama: String,
        username: String,
        pin: String,
        umur: Int,
        kelas: String?,
        gender: String?,
        agama: String?,
        onSelesai: () -> Unit,
    ) {
        val token = state.value.token ?: return
        state.value = state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val res = api.anakCreate(
                    "Bearer $token",
                    AnakCreateReq(nama, username, pin, umur, kelas, gender, agama),
                )
                state.value = state.value.copy(loading = false)
                if (res.isSuccessful) {
                    muatAnak(token)
                    onSelesai()
                } else {
                    val msg = when (res.code()) {
                        409 -> "Username sudah dipakai"
                        402 -> "Kuota anak aktif penuh. Upgrade ke ZX Elite."
                        400 -> "Data tidak valid"
                        else -> "Gagal (${res.code()})"
                    }
                    state.value = state.value.copy(error = msg)
                }
            } catch (e: Exception) {
                state.value = state.value.copy(loading = false, error = "Jaringan error: ${e.message}")
            }
        }
    }

    fun buatTugas(
        anakId: String,
        judul: String,
        deskripsi: String?,
        tokenReward: Int,
        deadline: String?,
        onSelesai: () -> Unit,
    ) {
        val token = state.value.token ?: return
        state.value = state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val res = api.tugasCreate(
                    "Bearer $token",
                    TugasCreateReq(anakId, judul, deskripsi, tokenReward, deadline),
                )
                state.value = state.value.copy(loading = false)
                if (res.isSuccessful) {
                    muatTugas(token)
                    onSelesai()
                } else {
                    state.value = state.value.copy(error = "Gagal buat tugas (${res.code()})")
                }
            } catch (e: Exception) {
                state.value = state.value.copy(loading = false, error = "Jaringan error: ${e.message}")
            }
        }
    }

    fun validasiTugas(tugasId: String, aksi: String) {
        val token = state.value.token ?: return
        viewModelScope.launch {
            try {
                val res = api.tugasValidasi("Bearer $token", tugasId, TugasValidasiReq(aksi))
                if (res.isSuccessful) {
                    muatTugas(token)
                    muatAnak(token) // token balance berubah kalau "selesai"
                }
            } catch (_: Exception) {}
        }
    }

    fun patchDevice(deviceId: String, patch: DevicePatch) {
        val token = state.value.token ?: return
        viewModelScope.launch {
            try {
                val res = api.devicePatch("Bearer $token", deviceId, patch)
                if (res.isSuccessful) {
                    muatDevices(token)
                }
            } catch (_: Exception) {}
        }
    }

    fun hapusDevice(deviceId: String) {
        val token = state.value.token ?: return
        viewModelScope.launch {
            try {
                api.deviceDelete("Bearer $token", deviceId)
                muatDevices(token)
            } catch (_: Exception) {}
        }
    }

    fun hapusAkun(onSelesai: () -> Unit) {
        val token = state.value.token ?: return
        viewModelScope.launch {
            try {
                api.akunHapus("Bearer $token")
                logout()
                onSelesai()
            } catch (_: Exception) {}
        }
    }

    fun logout() {
        viewModelScope.launch {
            simpanan.hapus()
            state.value = UiState()
        }
    }

    // ===== F4: Forum =====

    fun muatForum(token: String?) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.forumList("Bearer $token")
                if (res.isSuccessful) {
                    state.value = state.value.copy(forumPosts = res.body() ?: emptyList())
                }
            } catch (_: Exception) {}
        }
    }

    fun buatPost(judul: String, isi: String, kategori: String) {
        val token = state.value.token ?: return
        state.value = state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val res = api.forumCreate("Bearer $token", ForumCreateReq(judul, isi, kategori))
                state.value = state.value.copy(loading = false)
                if (res.isSuccessful) muatForum(token)
                else state.value = state.value.copy(error = "Gagal buat post (${res.code()})")
            } catch (e: Exception) {
                state.value = state.value.copy(loading = false, error = "Jaringan error: ${e.message}")
            }
        }
    }

    fun muatForumDetail(token: String?, postId: String) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.forumDetail("Bearer $token", postId)
                if (res.isSuccessful) {
                    state.value = state.value.copy(forumDetail = res.body())
                }
            } catch (_: Exception) {}
        }
    }

    fun hapusPost(postId: String) {
        val token = state.value.token ?: return
        viewModelScope.launch {
            try {
                api.forumDelete("Bearer $token", postId)
                muatForum(token)
            } catch (_: Exception) {}
        }
    }

    fun tambahKomentar(postId: String, isi: String) {
        val token = state.value.token ?: return
        viewModelScope.launch {
            try {
                api.forumKomentar("Bearer $token", postId, KomentarReq(isi))
                muatForumDetail(token, postId)
            } catch (_: Exception) {}
        }
    }

    fun clearForumDetail() {
        state.value = state.value.copy(forumDetail = null)
    }

    // ===== F10: Pesan (Direct Chat) =====

    fun muatPesan(token: String?) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.pesanList("Bearer $token")
                if (res.isSuccessful) {
                    state.value = state.value.copy(pesanList = res.body() ?: emptyList())
                }
            } catch (_: Exception) {}
        }
    }

    fun muatThread(token: String?, partnerId: String) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.pesanThread("Bearer $token", partnerId)
                if (res.isSuccessful) {
                    state.value = state.value.copy(pesanThread = res.body() ?: emptyList())
                }
            } catch (_: Exception) {}
        }
    }

    fun kirimPesan(penerimaId: String, isi: String) {
        val token = state.value.token ?: return
        viewModelScope.launch {
            try {
                api.pesanKirim("Bearer $token", PesanKirimReq(penerimaId, isi))
                muatThread(token, penerimaId)
            } catch (_: Exception) {}
        }
    }

    // ===== F11: AI Insights =====

    fun muatInsights(token: String?, anakId: String) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.insights("Bearer $token", anakId)
                if (res.isSuccessful) {
                    state.value = state.value.copy(insights = res.body())
                }
            } catch (_: Exception) {}
        }
    }

    // ===== F9: Pinjam Waktu =====

    fun muatPinjam(token: String?, anakId: String) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.pinjamRiwayat("Bearer $token", anakId)
                if (res.isSuccessful) {
                    state.value = state.value.copy(pinjam = res.body())
                }
            } catch (_: Exception) {}
        }
    }

    // ===== Referral =====

    fun muatReferral(token: String?) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.referralInfo("Bearer $token")
                if (res.isSuccessful) {
                    state.value = state.value.copy(referral = res.body())
                }
            } catch (_: Exception) {}
        }
    }

    fun klaimReferral(token: String?, kode: String, onResult: (String) -> Unit) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.referralClaim("Bearer $token", ReferralClaimReq(kode))
                if (res.isSuccessful) {
                    onResult("Berhasil! +${res.body()?.totalBonusCoin} Coin")
                    muatReferral(token)
                } else {
                    onResult("Gagal: ${res.code()}")
                }
            } catch (e: Exception) {
                onResult("Error: ${e.message}")
            }
        }
    }

    // ===== Leaderboard =====

    fun muatLeaderboard(token: String?, scope: String = "global") {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.leaderboard("Bearer $token", scope)
                if (res.isSuccessful) {
                    state.value = state.value.copy(leaderboard = res.body())
                }
            } catch (_: Exception) {}
        }
    }

    // ===== Daily Challenges =====

    fun setClaimResult(msg: String) {
        state.value = state.value.copy(claimResult = msg)
    }

    fun muatChallenges(token: String?, anakId: String) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.challenges("Bearer $token", anakId)
                if (res.isSuccessful) {
                    state.value = state.value.copy(challenges = res.body())
                }
            } catch (_: Exception) {}
        }
    }

    fun klaimChallenge(token: String?, anakId: String, challengeId: String, onResult: (String) -> Unit) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val res = api.challengeClaim("Bearer $token", ChallengeClaimReq(anakId, challengeId))
                if (res.isSuccessful) {
                    val body = res.body()
                    onResult("✓ ${body?.nama}: +${body?.reward} token")
                    muatChallenges(token, anakId)
                } else {
                    onResult("Gagal klaim: ${res.code()}")
                }
            } catch (e: Exception) {
                onResult("Error: ${e.message}")
            }
        }
    }
}
