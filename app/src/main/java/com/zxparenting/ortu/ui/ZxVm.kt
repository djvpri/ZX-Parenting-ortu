package com.zxparenting.ortu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zxparenting.ortu.api.AktivitasRes
import com.zxparenting.ortu.api.Anak
import com.zxparenting.ortu.api.AnakCreateReq
import com.zxparenting.ortu.api.ApiZx
import com.zxparenting.ortu.api.Device
import com.zxparenting.ortu.api.DevicePatch
import com.zxparenting.ortu.api.DaftarReq
import com.zxparenting.ortu.api.GoogleReq
import com.zxparenting.ortu.api.Klien
import com.zxparenting.ortu.api.LoginReq
import com.zxparenting.ortu.api.LoginRes
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

    // Muat semua data awal: devices + anak + tugas.
    fun muatSemua(token: String?) {
        if (token == null) return
        viewModelScope.launch {
            muatDevices(token)
            muatAnak(token)
            muatTugas(token)
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
}
