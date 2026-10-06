package com.zxparenting.ortu

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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zxparenting.ortu.data.Simpanan
import com.zxparenting.ortu.ui.ZxVm
import com.zxparenting.ortu.ui.ZxVmFactory
import com.zxparenting.ortu.ui.layar.LayarBeranda
import com.zxparenting.ortu.ui.layar.LayarLogin
import com.zxparenting.ortu.ui.layar.LayarNotif
import com.zxparenting.ortu.ui.layar.LayarPerangkat
import com.zxparenting.ortu.ui.layar.LayarProfil
import com.zxparenting.ortu.ui.tema.Bg
import com.zxparenting.ortu.ui.tema.TemaZX

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaZX {
                val simpanan = remember { Simpanan(this) }
                val vm: ZxVm = viewModel(factory = ZxVmFactory(simpanan))
                val state by vm.state.collectAsState()

                if (!state.loginOk) {
                    LayarLogin(state) { email, pass -> vm.login(email, pass) }
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
}

enum class BottomTab(val label: String, val ikon: ImageVector) {
    Beranda("Beranda", Icons.Default.Home),
    Perangkat("Perangkat", Icons.Default.Devices),
    Notifikasi("Notifikasi", Icons.Default.Notifications),
    Profil("Profil", Icons.Default.Person),
}
