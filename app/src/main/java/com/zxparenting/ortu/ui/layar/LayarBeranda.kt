package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.zxparenting.ortu.api.Device
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarBeranda(
    nama: String?,
    devices: List<Device>,
    onKlikPerangkat: () -> Unit,
    onKlikNotif: () -> Unit,
    onKlikLaporan: () -> Unit,
    onKlikProfil: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
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

        // Hero kartu anak (gradient)
        if (devices.isNotEmpty()) {
            KartuHeroAnak(devices.first())
        } else {
            KartuClay {
                Text(
                    "Belum ada perangkat terhubung.\nPasang app agent di HP anak lalu lakukan pairing.",
                    fontSize = 12.sp,
                    color = MutedFg,
                )
            }
        }

        // Grid menu 2x2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TileMenu(
                judul = "Batas Waktu",
                sub = "Aturan sesi",
                warnaBg = Color(0xFFEFF6FF),
                ikon = { IkonTinted(Icons.Default.Schedule, Biru) },
                modifier = Modifier.weight(1f),
            )
            TileMenu(
                judul = "Lokasi",
                sub = if (devices.any { it.gpsAktif == true }) "Aktif" else "Mati",
                warnaBg = Color(0xFFECFDF5),
                ikon = { IkonTinted(Icons.Default.LocationOn, Hijau) },
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TileMenu(
                judul = "Notifikasi",
                sub = "Alert zona",
                warnaBg = Color(0xFFFEF3C7),
                ikon = { IkonTinted(Icons.Default.Notifications, Amber) },
                modifier = Modifier.weight(1f),
            )
            TileMenu(
                judul = "Marketplace",
                sub = "Hadiah anak",
                warnaBg = Color(0xFFFCE7F3),
                ikon = { IkonTinted(Icons.Default.CardGiftcard, Pink) },
                modifier = Modifier.weight(1f),
            )
        }

        // Ringkasan perangkat singkat
        if (devices.isNotEmpty()) {
            KartuClay(kecil = true) {
                Column {
                    Text("Perangkat", fontSize = 10.sp, color = MutedFg, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    devices.take(3).forEach { dev ->
                        BarisKV(
                            k = dev.nama,
                            v = dev.anak?.nama ?: "-",
                            vWarna = if (dev.gpsAktif == true) Hijau else MutedFg,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun KartuHeroAnak(device: Device) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(Biru, Ungu)))
            .padding(18.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        (device.anak?.nama?.firstOrNull() ?: "?").toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        device.anak?.nama ?: "Anak",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                    Text(
                        device.nama,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 10.5.sp,
                    )
                }
                BadgePill(teks = "Aktif", bg = Color(0xFFDCFCE7), fg = Color(0xFF166534))
            }
            Spacer(Modifier.height(14.dp))
            Text("Waktu layar hari ini", color = Color.White.copy(alpha = 0.8f), fontSize = 9.5.sp)
            Spacer(Modifier.height(4.dp))
            BarMini(
                persen = 0.52f,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White.copy(alpha = 0.25f)),
            )
        }
    }
}

@Composable
fun IkonTinted(ikon: ImageVector, warna: Color) {
    Icon(ikon, contentDescription = null, tint = warna, modifier = Modifier.size(18.dp))
}
