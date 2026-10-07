package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.AktivitasRes
import com.zxparenting.ortu.api.Anak
import com.zxparenting.ortu.api.Device
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarLaporan(
    anakList: List<Anak>,
    aktivitas: AktivitasRes?,
    onPilihAnak: (anakId: String) -> Unit,
) {
    var dropdown by remember { mutableStateOf(false) }
    var anakTerpilih by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Laporan", style = MaterialTheme.typography.titleLarge)
        }

        // Dropdown pilih anak
        if (anakList.isNotEmpty()) {
            Box {
                OutlinedButton(
                    onClick = { dropdown = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(anakList[anakTerpilih].nama)
                }
                DropdownMenu(expanded = dropdown, onDismissRequest = { dropdown = false }) {
                    anakList.forEachIndexed { i, a ->
                        DropdownMenuItem(
                            text = { Text(a.nama) },
                            onClick = {
                                anakTerpilih = i
                                dropdown = false
                                onPilihAnak(a.id)
                            },
                        )
                    }
                }
            }
        } else {
            KartuClay { Text("Tambah anak dulu untuk melihat laporan.", fontSize = 12.sp, color = MutedFg) }
        }

        if (aktivitas != null) {
            // Ringkasan 7 hari
            if (aktivitas.ringkasan7hari.isNotEmpty()) {
                Text("7 Hari Terakhir", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
                KartuClay {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        aktivitas.ringkasan7hari.forEach { r ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(terjemahTipe(r.tipe), fontSize = 12.sp)
                                Text("${r.jumlah}x", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Biru)
                            }
                        }
                    }
                }
            }

            // Log aktivitas
            if (aktivitas.logs.isNotEmpty()) {
                Text("Riwayat Aktivitas", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
                aktivitas.logs.take(30).forEach { log ->
                    KartuClay(kecil = true) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Biru.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Default.Analytics, contentDescription = null, tint = Biru, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(terjemahTipe(log.tipe), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                if (!log.detail.isNullOrBlank()) {
                                    Text(log.detail, fontSize = 10.sp, color = MutedFg)
                                }
                                Text(log.createdAt.take(16).replace("T", " "), fontSize = 9.sp, color = MutedFg)
                            }
                        }
                    }
                }
            } else {
                KartuClay { Text("Belum ada aktivitas tercatat.", fontSize = 12.sp, color = MutedFg) }
            }
        } else if (anakList.isNotEmpty()) {
            KartuClay {
                Text("Pilih anak untuk melihat laporan.", fontSize = 12.sp, color = MutedFg)
            }
        }
    }
}

private fun terjemahTipe(tipe: String): String = when (tipe) {
    "session_start" -> "Mulai Sesi"
    "session_end" -> "Selesai Sesi"
    "geo_exit" -> "Keluar Zona"
    "geo_enter" -> "Masuk Zona"
    "app_blocked" -> "App Diblokir"
    "task_done" -> "Tugas Selesai"
    "task_rejected" -> "Tugas Ditolak"
    "curfew_start" -> "Jam Tidur"
    "master_unlock" -> "Master Unlock"
    else -> tipe
}
