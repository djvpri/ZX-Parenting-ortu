package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.Device
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarPerangkat(devices: List<Device>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text("Perangkat", style = MaterialTheme.typography.titleLarge)
        if (devices.isEmpty()) {
            KartuClay {
                Text("Belum ada perangkat.", fontSize = 12.sp, color = MutedFg)
            }
        }
        devices.forEach { dev ->
            KartuClay(kecil = true) {
                Column {
                    Text(dev.nama, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(dev.anak?.nama ?: "-", fontSize = 11.sp, color = MutedFg)
                    Spacer(Modifier.height(8.dp))
                    BarisKV("Sesi limit", "${dev.sessionLimit ?: 0} menit")
                    BarisKV(
                        "GPS",
                        if (dev.gpsAktif == true) "Aktif" else "Mati",
                        vWarna = if (dev.gpsAktif == true) Hijau else MutedFg,
                    )
                    BarisKV(
                        "Geo-fence",
                        if (dev.geoAktif == true) "Aktif" else "Mati",
                        vWarna = if (dev.geoAktif == true) Hijau else MutedFg,
                    )
                    if (dev.appBlokir?.isNotEmpty() == true) {
                        BarisKV("App diblokir", "${dev.appBlokir.size} app")
                    }
                }
            }
        }
    }
}
