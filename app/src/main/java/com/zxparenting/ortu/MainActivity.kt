package com.zxparenting.ortu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Devices
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
import com.zxparenting.ortu.ui.layar.LayarBeranda
import com.zxparenting.ortu.ui.layar.LayarDaftar
import com.zxparenting.ortu.ui.layar.LayarLogin
import com.zxparenting.ortu.ui.layar.LayarNotif
import com.zxparenting.ortu.ui.layar.LayarPerangkat
import com.zxparenting.ortu.ui.layar.LayarProfil
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

                // Dialog update tersedia
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
                                onKlikPerangkat = { tab = 1 },
                                onKlikNotif = { tab = 2 },
                                onKlikLaporan = { tab = 3 },
                                onKlikProfil = { tab = 3 },
                            )
                            1 -> LayarPerangkat(state.devices)
                            2 -> LayarNotif()
                            3 -> LayarProfil(state.nama, vm::logout)
                        }
                    }
                }
            }
        }
    }

    // In-App Update — cek Play Store saat app dibuka.
    // Flexible: download di background, dialog tanya install ulang.
    // Kalau Play Core tak tersedia (sideload), fallback buka halaman Play Store.
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

    // Credential Manager — dapatkan Google ID token untuk login native.
    // Web Client ID = AUTH_GOOGLE_ID server (audience yang diverifikasi backend).
    // Strategi: GetGoogleIdOption dulu; kalau "no credentials available"
    // (akun Google tak ter-link ke Credential Manager), fallback ke
    // GetSignInWithGoogleOption yang langsung tampilkan account picker.
    private suspend fun ambilGoogleIdToken(errorMsg: (String) -> Unit): String? {
        val cm = CredentialManager.create(this)

        // 1. Credential Manager standar
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

        // 2. Fallback: GetSignInWithGoogleOption (account picker eksplisit)
        try {
            val signInOption = GetSignInWithGoogleOption.Builder(GOOGLE_WEB_ID)
                .build()
            val req2 = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()
            val res2 = cm.getCredential(this, req2)
            return GoogleIdTokenCredential.createFrom(res2.credential.data).idToken
        } catch (e: Exception) {
            // Error code 16 = "account reauth failed" — SHA-1 signing key belum
            // terdaftar di Google Cloud Android OAuth Client, atau google-services.json
            // belum ada. Tampilkan error mentah agar user tahu penyebabnya.
            val msg = e.message ?: e.toString()
            errorMsg("Google gagal [$msg]. Pastikan app terinstall dari Play Store atau hubungi developer.")
            return null
        }
    }

    companion object {
        // Web Client ID Google = AUTH_GOOGLE_ID di Coolify app 9.
        private const val GOOGLE_WEB_ID =
            "117197293834-5do5mam50v62vn5d4rc8mpj80rfm97gh.apps.googleusercontent.com"
        private const val KODE_UPDATE = 1001
    }
}

enum class BottomTab(val label: String, val ikon: ImageVector) {
    Beranda("Beranda", Icons.Default.Home),
    Perangkat("Perangkat", Icons.Default.Devices),
    Notifikasi("Notifikasi", Icons.Default.Notifications),
    Profil("Profil", Icons.Default.Person),
}
