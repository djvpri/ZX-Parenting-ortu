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
    coinSaldo: Int,
    tierLabel: String,
    onKlikAnak: () -> Unit,
    onKlikTugas: () -> Unit,
    onKlikAturan: () -> Unit,
    onKlikLokasi: () -> Unit,
    onKlikLaporan: () -> Unit,
    onKlikMarket: () -> Unit,
    onKlikCoin: () -> Unit,
    onKlikQuest: () -> Unit,
    onKlikForum: () -> Unit,
    onKlikLangganan: () -> Unit,
    onKlikPesan: () -> Unit,
    onKlikInsights: () -> Unit,
    onKlikPinjam: () -> Unit,
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
            BadgePill(teks = "$coinSaldo ZX", bg = Color(0xFFDCFCE7), fg = Color(0xFF166534))
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

        // Grid menu 3x3
        Spacer(Modifier.height(4.dp))
        Text("Menu", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)

        // Row 1: Anak, Tugas, Market
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TileMenu(Teks["anak"], "${anakList.size} ${Teks["profil_jumlah"]}", Biru, Icons.Default.ChildCare, Modifier.weight(1f), onKlikAnak)
            TileMenu(Teks["tugas"], "${tugasList.size} ${Teks["tugas_jumlah"]}", Hijau, Icons.Default.Assignment, Modifier.weight(1f), onKlikTugas)
            TileMenu(Teks["market"], Teks["hadiah"], Pink, Icons.Default.ShoppingBag, Modifier.weight(1f), onKlikMarket)
        }

        // Row 2: Aturan, Lokasi, Laporan
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TileMenu(Teks["aturan"], "${devices.size} ${Teks["perangkat_jumlah"]}", Ungu, Icons.Default.Tune, Modifier.weight(1f), onKlikAturan)
            TileMenu(Teks["lokasi"], Teks["gps"], Merah, Icons.Default.LocationOn, Modifier.weight(1f), onKlikLokasi)
            TileMenu(Teks["laporan"], Teks["aktivitas"], Amber, Icons.Default.Analytics, Modifier.weight(1f), onKlikLaporan)
        }

        // Row 3: Coin, Quest, Forum
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TileMenu(Teks["coin"], "$coinSaldo ZX", Hijau, Icons.Default.AccountBalanceWallet, Modifier.weight(1f), onKlikCoin)
            TileMenu(Teks["quest"], Teks["keluarga"], Ungu, Icons.Default.Flag, Modifier.weight(1f), onKlikQuest)
            TileMenu(Teks["forum"], Teks["diskusi"], Biru, Icons.Default.Forum, Modifier.weight(1f), onKlikForum)
        }

        // Row 4: Langganan, Pesan, Insights
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TileMenu(Teks["langganan"], tierLabel, Amber, Icons.Default.Star, Modifier.weight(1f), onKlikLangganan)
            TileMenu(Teks["pesan"], Teks["chat"], Hijau, Icons.Default.Chat, Modifier.weight(1f), onKlikPesan)
            TileMenu(Teks["insights"], Teks["ai"], Ungu, Icons.Default.Insights, Modifier.weight(1f), onKlikInsights)
        }

        // Row 5: Pinjam Waktu
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TileMenu(Teks["pinjam_waktu"], Teks["hutang"], Amber, Icons.Default.Schedule, Modifier.weight(1f), onKlikPinjam)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TileMenu(
    judul: String,
    sub: String,
    warna: Color,
    ikon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = warna.copy(alpha = 0.08f)),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(ikon, contentDescription = null, tint = warna, modifier = Modifier.size(20.dp))
            Column {
                Text(judul, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(sub, fontSize = 9.sp, color = MutedFg)
            }
        }
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
