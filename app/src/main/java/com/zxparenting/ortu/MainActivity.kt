package com.zxparenting.ortu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.zxparenting.ortu.data.Simpanan
import com.zxparenting.ortu.ui.ZxVm
import com.zxparenting.ortu.ui.ZxVmFactory
import com.zxparenting.ortu.ui.layar.LayarAnak
import com.zxparenting.ortu.ui.layar.LayarAturan
import com.zxparenting.ortu.ui.layar.LayarBeranda
import com.zxparenting.ortu.ui.layar.LayarDaftar
import com.zxparenting.ortu.ui.layar.LayarLaporan
import com.zxparenting.ortu.ui.layar.LayarLokasi
import com.zxparenting.ortu.ui.layar.LayarLogin
import com.zxparenting.ortu.ui.layar.LayarProfil
import com.zxparenting.ortu.ui.layar.LayarTugas
import com.zxparenting.ortu.ui.tema.Bg
import com.zxparenting.ortu.ui.tema.TemaZX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        enableEdgeToEdge()
        cekUpdate()
        setContent {
            TemaZX {
                val simpanan = remember { Simpanan(this) }
                val vm: ZxVm = viewModel(factory = ZxVmFactory(simpanan))
                val state by vm.state.collectAsState()
                val scope = rememberCoroutineScope()

                if (tanyaUpdate) {
                    AlertDialog(
                        onDismissRequest = { tanyaUpdate = false },
                        title = { Text("Update tersedia") },
                        text = { Text("Versi baru tersedia di Play Store. Update sekarang?") },
                        confirmButton = {
                            TextButton(onClick = {
                                tanyaUpdate = false
                                mulaiUpdate()
                            }) { Text("Update") }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                tanyaUpdate = false
                                bukaPlayStore()
                            }) { Text("Buka Play Store") }
                        },
                    )
                }

                if (!state.loginOk) {
                    var modeDaftar by remember { mutableStateOf(false) }
                    if (modeDaftar) {
                        LayarDaftar(state, onDaftar = { nama, email, pass -> vm.daftar(nama, email, pass) }) { modeDaftar = false }
                    } else {
                        LayarLogin(
                            state,
                            onLogin = { email, pass -> vm.login(email, pass) },
                            onKeDaftar = { modeDaftar = true },
                            onGoogle = {
                                scope.launch {
                                    val idToken = ambilGoogleIdToken { err ->
                                        vm.setError(err)
                                    }
                                    if (idToken != null) {
                                        vm.loginGoogle(idToken, GOOGLE_WEB_ID)
                                    }
                                }
                            },
                        )
                    }
                    return@TemaZX
                }

                var tab by remember { mutableIntStateOf(0) }
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            BottomTab.entries.forEachIndexed { i, t ->
                                NavigationBarItem(
                                    selected = tab == i,
                                    onClick = { tab = i },
                                    icon = { Icon(t.ikon, contentDescription = t.label) },
                                    label = { Text(t.label) },
                                )
                            }
                        }
                    },
                ) { pad ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Bg)
                            .padding(pad),
                    ) {
                        when (tab) {
                            0 -> LayarBeranda(
                                nama = state.nama,
                                devices = state.devices,
                                anakList = state.anakList,
                                tugasList = state.tugasList,
                                onKlikAnak = { tab = 1 },
                                onKlikTugas = { tab = 2 },
                                onKlikAturan = { tab = 3 },
                                onKlikProfil = { tab = 4 },
                            )
                            1 -> LayarAnak(
                                anakList = state.anakList,
                                loading = state.loading,
                                onBuat = { n, u, p, um, k, g, a ->
                                    vm.buatAnak(n, u, p, um, k, g, a) {}
                                },
                            )
                            2 -> LayarTugas(
                                tugasList = state.tugasList,
                                anakList = state.anakList,
                                loading = state.loading,
                                onBuat = { aId, j, d, r -> vm.buatTugas(aId, j, d, r, null) {} },
                                onValidasi = { id, aksi -> vm.validasiTugas(id, aksi) },
                            )
                            3 -> LayarAturan(
                                devices = state.devices,
                                onPatch = { id, patch -> vm.patchDevice(id, patch) },
                                onDelete = { id -> vm.hapusDevice(id) },
                            )
                            4 -> LayarLokasi(
                                devices = state.devices,
                                onPatch = { id, patch -> vm.patchDevice(id, patch) },
                            )
                            5 -> LayarLaporan(
                                anakList = state.anakList,
                                aktivitas = state.aktivitas,
                                onPilihAnak = { id -> vm.muatAktivitas(state.token, id) },
                            )
                            6 -> LayarProfil(
                                nama = state.nama,
                                onLogout = vm::logout,
                                onHapusAkun = { vm.hapusAkun {} },
                            )
                        }
                    }
                }
            }
        }
    }

    private var tanyaUpdate by mutableStateOf(false)

    private fun cekUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                tanyaUpdate = true
            }
        }
    }

    private fun mulaiUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    AppUpdateType.FLEXIBLE,
                    this,
                    KODE_UPDATE,
                )
            }
        }
    }

    private fun bukaPlayStore() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (_: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    private suspend fun ambilGoogleIdToken(errorMsg: (String) -> Unit): String? {
        val cm = CredentialManager.create(this)

        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(GOOGLE_WEB_ID)
                .build()
            val req = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val res = cm.getCredential(this, req)
            return GoogleIdTokenCredential.createFrom(res.credential.data).idToken
        } catch (_: Exception) { /* lanjut fallback */ }

        try {
            val signInOption = GetSignInWithGoogleOption.Builder(GOOGLE_WEB_ID)
                .build()
            val req2 = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()
            val res2 = cm.getCredential(this, req2)
            return GoogleIdTokenCredential.createFrom(res2.credential.data).idToken
        } catch (e: Exception) {
            val msg = e.message ?: e.toString()
            errorMsg("Google gagal [$msg]. Pastikan app terinstall dari Play Store atau hubungi developer.")
            return null
        }
    }

    companion object {
        private const val GOOGLE_WEB_ID =
            "117197293834-5do5mam50v62vn5d4rc8mpj80rfm97gh.apps.googleusercontent.com"
        private const val KODE_UPDATE = 1001
    }
}

enum class BottomTab(val label: String, val ikon: ImageVector) {
    Beranda("Beranda", Icons.Default.Home),
    Anak("Anak", Icons.Default.ChildCare),
    Tugas("Tugas", Icons.Default.Assignment),
    Aturan("Aturan", Icons.Default.Devices),
    Lokasi("Lokasi", Icons.Default.LocationOn),
    Laporan("Laporan", Icons.Default.Analytics),
    Profil("Profil", Icons.Default.Person),
}
