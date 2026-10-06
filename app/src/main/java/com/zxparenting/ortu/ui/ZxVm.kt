package com.zxparenting.ortu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zxparenting.ortu.api.AktivitasRes
import com.zxparenting.ortu.api.ApiZx
import com.zxparenting.ortu.api.Device
import com.zxparenting.ortu.api.DaftarReq
import com.zxparenting.ortu.api.GoogleReq
import com.zxparenting.ortu.api.Klien
import com.zxparenting.ortu.api.LoginReq
import com.zxparenting.ortu.api.LoginRes
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
    val devices: List<Device> = emptyList(),
    val aktivitas: AktivitasRes? = null,
)

class ZxVm(val simpanan: Simpanan) : ViewModel() {
    private val api: ApiZx = Klien.api
    val state = MutableStateFlow(UiState())
    val s: StateFlow<UiState> get() = state

    init {
        // cek token tersimpan
        viewModelScope.launch {
            val t = simpanan.token.first()
            if (t != null) {
                state.value = state.value.copy(loginOk = true, nama = simpanan.nama.first())
                muatDevices(t)
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

    private suspend fun suksesLogin(body: LoginRes) {
        simpanan.simpanSesi(body.token, body.user.nama, body.user.id)
        state.value = state.value.copy(
            loading = false,
            loginOk = true,
            nama = body.user.nama,
        )
        muatDevices(body.token)
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

    fun logout() {
        viewModelScope.launch {
            simpanan.hapus()
            state.value = UiState()
        }
    }
}
