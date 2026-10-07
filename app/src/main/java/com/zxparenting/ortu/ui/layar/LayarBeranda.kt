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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.Anak
import com.zxparenting.ortu.api.Device
import com.zxparenting.ortu.api.Tugas
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarBeranda(
    nama: String?,
    devices: List<Device>,
    anakList: List<Anak>,
    tugasList: List<Tugas>,
    onKlikAnak: () -> Unit,
    onKlikTugas: () -> Unit,
    onKlikAturan: () -> Unit,
    onKlikProfil: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(14.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Halo, $nama", style = MaterialTheme.typography.titleLarge)
                Text("ZX Parenting", fontSize = 12.sp, color = MutedFg)
            }
            BadgePill(teks = "Aktif", bg = Color(0xFFDCFCE7), fg = Color(0xFF166534))
        }

        // Ringkasan anak
        if (anakList.isNotEmpty()) {
            Text("Anak", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
            anakList.take(3).forEach { anak ->
                KartuRingkasanAnak(anak)
            }
        } else {
            KartuClay {
                Column {
                    Text("Belum ada anak", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Tambah profil anak untuk mulai.", fontSize = 11.sp, color = MutedFg)
                }
            }
        }

        // Tugas menunggu validasi
        val menunggu = tugasList.filter { it.status == "MENUNGGU" || it.status == "PENDING" }
        if (menunggu.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Perlu Validasi", fontSize = 11.sp, color = Amber, fontWeight = FontWeight.Bold)
                TextButton(onClick = onKlikTugas) { Text("Lihat semua", fontSize = 11.sp) }
            }
            menunggu.take(3).forEach { t ->
                KartuTugasSingkat(t)
            }
        }

        // Grid menu 2x2
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TileMenu(
                judul = "Anak",
                sub = "${anakList.size} profil",
                warnaBg = Color(0xFFEFF6FF),
                ikon = { IkonTinted(Icons.Default.ChildCare, Biru) },
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(18.dp)),
            )
            TileMenu(
                judul = "Tugas",
                sub = "${tugasList.size} tugas",
                warnaBg = Color(0xFFECFDF5),
                ikon = { IkonTinted(Icons.Default.Assignment, Hijau) },
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(18.dp)),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TileMenu(
                judul = "Aturan",
                sub = "${devices.size} perangkat",
                warnaBg = Color(0xFFFCE7F3),
                ikon = { IkonTinted(Icons.Default.Tune, Pink) },
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(18.dp)),
            )
            TileMenu(
                judul = "Profil",
                sub = "Pengaturan",
                warnaBg = Color(0xFFFEF3C7),
                ikon = { IkonTinted(Icons.Default.Person, Amber) },
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(18.dp)),
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun KartuRingkasanAnak(anak: Anak) {
    KartuClay(kecil = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(Biru, Ungu))),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    (anak.nama.firstOrNull() ?: "?").toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(anak.nama, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("${anak.umur ?: "?"} tahun", fontSize = 10.sp, color = MutedFg)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${anak.tokenBalance?.balance ?: 0}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Hijau,
                )
                Text("Token", fontSize = 9.sp, color = MutedFg)
            }
        }
    }
}

@Composable
private fun KartuTugasSingkat(tugas: Tugas) {
    KartuClay(kecil = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Assignment, contentDescription = null, tint = Amber, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tugas.judul, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(tugas.anak.nama, fontSize = 10.sp, color = MutedFg)
            }
            BadgePill(teks = "${tugas.tokenReward}", bg = Color(0xFFDCFCE7), fg = Color(0xFF166534))
        }
    }
}

@Composable
fun IkonTinted(ikon: ImageVector, warna: Color) {
    Icon(ikon, contentDescription = null, tint = warna, modifier = Modifier.size(18.dp))
}
