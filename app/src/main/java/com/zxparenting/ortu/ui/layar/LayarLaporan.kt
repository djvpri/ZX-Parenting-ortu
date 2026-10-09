package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.*
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarLaporan(
    anakList: List<Anak>,
    aktivitas: AktivitasRes?,
    ringkasan: LaporanRingkasanRes?,
    insights: InsightsRes?,
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
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text("Laporan", style = MaterialTheme.typography.titleLarge)

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

        // 4 Kartu Stat
        if (ringkasan != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                KartuStatMini(
                    modifier = Modifier.weight(1f),
                    ikon = Icons.Default.CheckCircle,
                    warnaIkon = Hijau,
                    label = "Tugas Selesai",
                    nilai = "${ringkasan.kartu.tugasSelesai}/${ringkasan.kartu.tugasTotal}",
                    sub = "${ringkasan.kartu.completionRate}% completion",
                )
                KartuStatMini(
                    modifier = Modifier.weight(1f),
                    ikon = Icons.Default.Quiz,
                    warnaIkon = Biru,
                    label = "Skor Kuis",
                    nilai = "${ringkasan.kartu.skorKuis}",
                    sub = ringkasan.kartu.trenSkor?.let { if (it >= 0) "↑ +$it vs lalu" else "↓ $it vs lalu" } ?: "—",
                    subWarna = if ((ringkasan.kartu.trenSkor ?: 0) >= 0) Hijau else Merah,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                KartuStatMini(
                    modifier = Modifier.weight(1f),
                    ikon = Icons.Default.LocalFireDepartment,
                    warnaIkon = Amber,
                    label = "Streak",
                    nilai = "${ringkasan.kartu.streakCurrent} hari",
                    sub = "Terbaik: ${ringkasan.kartu.streakTerbaik}",
                )
                KartuStatMini(
                    modifier = Modifier.weight(1f),
                    ikon = Icons.Default.MonetizationOn,
                    warnaIkon = Amber,
                    label = "Saldo Token",
                    nilai = "${ringkasan.kartu.saldoToken} mnt",
                    sub = ringkasan.kartu.trenToken?.let { if (it >= 0) "↑ +$it" else "↓ $it" } ?: "—",
                    subWarna = if ((ringkasan.kartu.trenToken ?: 0) >= 0) Hijau else Merah,
                )
            }
        }

        // AI Insight
        if (insights != null && insights.insights.isNotEmpty()) {
            Text("AI Insight", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
            insights.insights.forEach { ins ->
                val (bg, fg, ikon) = when (ins.level) {
                    "warning" -> Triple(Merah.copy(alpha = 0.1f), Merah, Icons.Default.Warning)
                    "positive" -> Triple(Hijau.copy(alpha = 0.1f), Hijau, Icons.Default.CheckCircle)
                    else -> Triple(Biru.copy(alpha = 0.1f), Biru, Icons.Default.Info)
                }
                KartuClay(kecil = true) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(bg),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(ikon, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(ins.teks, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            if (ins.kategori.isNotBlank()) {
                                Text(ins.kategori, fontSize = 10.sp, color = MutedFg)
                            }
                        }
                    }
                }
            }
        }

        // Ringkasan Tugas
        if (ringkasan != null && ringkasan.tugasTerbaru.isNotEmpty()) {
            Text("Ringkasan Tugas", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
            KartuClay {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Progress bar completion
                    BarMini(ringkasan.kartu.completionRate / 100f)
                    Text("${ringkasan.kartu.completionRate}% selesai", fontSize = 10.sp, color = MutedFg)
                    Spacer(Modifier.height(4.dp))
                    ringkasan.tugasTerbaru.forEach { t ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(t.judul, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            val (badgeBg, badgeFg, badgeText) = when (t.status) {
                                "SELESAI" -> Triple(Hijau.copy(alpha = 0.15f), Hijau, "Selesai")
                                "PENDING" -> Triple(Biru.copy(alpha = 0.15f), Biru, "Aktif")
                                "DIKUMPULKAN", "MENUNGGU" -> Triple(Amber.copy(alpha = 0.15f), Amber, "Perlu Validasi")
                                "DITOLAK" -> Triple(Merah.copy(alpha = 0.15f), Merah, "Ditolak")
                                else -> Triple(Muted, MutedFg, t.status)
                            }
                            BadgePill(badgeText, badgeBg, badgeFg)
                        }
                    }
                }
            }
        }

        // Laporan Kuis AI
        if (ringkasan != null && ringkasan.laporanKuis.isNotEmpty()) {
            Text("Laporan Kuis AI", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
            KartuClay {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ringkasan.laporanKuis.forEach { k ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("Kuis", fontSize = 12.sp, color = MutedFg)
                                Text("${k.rataSkor}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(4.dp))
                            BarMini(k.rataSkor / 100f)
                            Text("${k.coba} percobaan", fontSize = 9.sp, color = MutedFg)
                        }
                    }
                }
            }
        }

        // Statistik Token
        if (ringkasan != null) {
            Text("Statistik Token", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
            KartuClay {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Hijau, modifier = Modifier.size(20.dp))
                        Text("+${ringkasan.tokenStat.masuk} mnt", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Hijau)
                        Text("Masuk", fontSize = 10.sp, color = MutedFg)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Merah, modifier = Modifier.size(20.dp))
                        Text("-${ringkasan.tokenStat.keluar} mnt", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Merah)
                        Text("Keluar", fontSize = 10.sp, color = MutedFg)
                    }
                }
            }
        }

        // Timeline Aktivitas
        if (aktivitas != null && aktivitas.logs.isNotEmpty()) {
            Text("Timeline Aktivitas", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
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
        } else if (ringkasan == null && aktivitas == null && anakList.isNotEmpty()) {
            KartuClay { Text("Pilih anak untuk melihat laporan.", fontSize = 12.sp, color = MutedFg) }
        }
    }
}

@Composable
private fun KartuStatMini(
    modifier: Modifier = Modifier,
    ikon: androidx.compose.ui.graphics.vector.ImageVector,
    warnaIkon: Color,
    label: String,
    nilai: String,
    sub: String,
    subWarna: Color = MutedFg,
) {
    KartuClay(modifier = modifier, kecil = true) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(ikon, contentDescription = null, tint = warnaIkon, modifier = Modifier.size(14.dp))
                Text(label, fontSize = 10.sp, color = MutedFg)
            }
            Text(nilai, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(sub, fontSize = 9.sp, color = subWarna)
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
