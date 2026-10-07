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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
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
import com.zxparenting.ortu.ui.layar.LayarCoin
import com.zxparenting.ortu.ui.layar.LayarDaftar
import com.zxparenting.ortu.ui.layar.LayarForum
import com.zxparenting.ortu.ui.layar.LayarLangganan
import com.zxparenting.ortu.ui.layar.LayarLaporan
import com.zxparenting.ortu.ui.layar.LayarLokasi
import com.zxparenting.ortu.ui.layar.LayarLogin
import com.zxparenting.ortu.ui.layar.LayarMarketplace
import com.zxparenting.ortu.ui.layar.LayarPesan
import com.zxparenting.ortu.ui.layar.LayarInsights
import com.zxparenting.ortu.ui.layar.LayarProfil
import com.zxparenting.ortu.ui.layar.LayarQuest
import com.zxparenting.ortu.ui.layar.LayarTugas
import com.zxparenting.ortu.ui.tema.Bg
import com.zxparenting.ortu.ui.tema.PengaturanTema
import com.zxparenting.ortu.ui.tema.TemaRepo
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
        TemaRepo.init(this)
        PengaturanTema.terapkan(this)
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

                // Navigasi: 5 bottom tab + sub-layar dari Beranda
                var tab by remember { mutableIntStateOf(0) }
                // subLayar: 0=none, 1=Aturan, 2=Lokasi, 3=Laporan, 4=Coin, 5=Quest
                var subLayar by remember { mutableIntStateOf(0) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            BottomTab.entries.forEachIndexed { i, t ->
                                NavigationBarItem(
                                    selected = tab == i && subLayar == 0,
                                    onClick = { tab = i; subLayar = 0 },
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
                        // Sub-layar override bottom tab
                        if (tab == 0 && subLayar > 0) {
                            when (subLayar) {
                                1 -> LayarAturan(
                                    devices = state.devices,
                                    onPatch = { id, patch -> vm.patchDevice(id, patch) },
                                    onDelete = { id -> vm.hapusDevice(id) },
                                )
                                2 -> LayarLokasi(
                                    devices = state.devices,
                                    onPatch = { id, patch -> vm.patchDevice(id, patch) },
                                )
                                3 -> LayarLaporan(
                                    anakList = state.anakList,
                                    aktivitas = state.aktivitas,
                                    onPilihAnak = { id -> vm.muatAktivitas(state.token, id) },
                                )
                                4 -> LayarCoin(
                                    coin = state.coin,
                                    loading = state.loading,
                                    onTopup = { j -> vm.topupCoin(j) },
                                )
                                5 -> LayarQuest(
                                    quests = state.quests,
                                    loading = state.loading,
                                    onBuat = { j, d, r -> vm.buatQuest(j, d, r) },
                                )
                                6 -> LayarForum(
                                    posts = state.forumPosts,
                                    detail = state.forumDetail,
                                    loading = state.loading,
                                    onBuatPost = { j, i, k -> vm.buatPost(j, i, k) },
                                    onBukaPost = { id -> vm.muatForumDetail(state.token, id) },
                                    onHapusPost = { id -> vm.hapusPost(id) },
                                    onKomentar = { id, isi -> vm.tambahKomentar(id, isi) },
                                    onBack = { vm.clearForumDetail() },
                                )
                                7 -> LayarLangganan(
                                    langganan = state.langganan,
                                    loading = state.loading,
                                )
                                8 -> LayarPesan(
                                    pesanList = state.pesanList,
                                    pesanThread = state.pesanThread,
                                    loading = state.loading,
                                    onBukaThread = { id -> vm.muatThread(state.token, id) },
                                    onKirim = { id, isi -> vm.kirimPesan(id, isi) },
                                    onBack = {},
                                )
                                9 -> LayarInsights(
                                    anakList = state.anakList,
                                    insights = state.insights,
                                    loading = state.loading,
                                    onPilihAnak = { id -> vm.muatInsights(state.token, id) },
                                )
                            }
                            return@Box
                        }

                        when (tab) {
                            0 -> LayarBeranda(
                                nama = state.nama,
                                devices = state.devices,
                                anakList = state.anakList,
                                tugasList = state.tugasList,
                                coinSaldo = state.coin?.saldo ?: 0,
                                tierLabel = {
                                    val lg = state.langganan
                                    when (lg?.tier) {
                                        "FREE_TRIAL" -> "Trial ${lg.hariSisa}h"
                                        "PRO" -> "Pro"
                                        "ELITE" -> "Elite"
                                        else -> "-"
                                    }
                                }(),
                                onKlikAnak = { tab = 1 },
                                onKlikTugas = { tab = 2 },
                                onKlikAturan = { subLayar = 1 },
                                onKlikLokasi = { subLayar = 2 },
                                onKlikLaporan = { subLayar = 3 },
                                onKlikMarket = { tab = 3 },
                                onKlikCoin = { subLayar = 4 },
                                onKlikQuest = { subLayar = 5 },
                                onKlikForum = { subLayar = 6 },
                                onKlikLangganan = { subLayar = 7 },
                                onKlikPesan = { subLayar = 8 },
                                onKlikInsights = { subLayar = 9 },
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
                            3 -> LayarMarketplace(
                                market = state.market,
                                coinSaldo = state.coin?.saldo ?: 0,
                                loading = state.loading,
                                onBuatHadiah = { j, d, h, s -> vm.buatHadiah(j, d, h, s) },
                                onHapusHadiah = { id -> vm.hapusHadiah(id) },
                                onProsesPesanan = { id, aksi -> vm.prosesPesanan(id, aksi) },
                            )
                            4 -> LayarProfil(
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
    Market("Market", Icons.Default.ShoppingBag),
    Profil("Profil", Icons.Default.Person),
}
