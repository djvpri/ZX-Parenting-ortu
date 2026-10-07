package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.Anak
import com.zxparenting.ortu.api.PinjamRes
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarPinjam(
    anakList: List<Anak>,
    pinjam: PinjamRes?,
    loading: Boolean,
    onPilihAnak: (String) -> Unit,
) {
    var selectedAnak by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(Bg).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text(Teks["pinjam_waktu"], style = MaterialTheme.typography.titleLarge)

        // Pilih anak
        KartuClay(kecil = true) {
            Column {
                Text(Teks["pilih_anak"], fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                anakList.forEach { anak ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(anak.nama, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        RadioButton(
                            selected = selectedAnak == anak.id,
                            onClick = {
                                selectedAnak = anak.id
                                onPilihAnak(anak.id)
                            },
                        )
                    }
                }
            }
        }

        if (loading && pinjam == null && selectedAnak != null) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        if (pinjam != null) {
            // Hutang card
            KartuClay {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (pinjam.hutang > 0) Merah else Hijau),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(Teks["hutang"], fontSize = 11.sp, color = MutedFg)
                        Text("${pinjam.hutang} ${Teks["menit"]}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (pinjam.hutang > 0) Merah else Hijau)
                    }
                }
            }

            // Riwayat
            Spacer(Modifier.height(4.dp))
            Text(Teks["riwayat_pinjam"], fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MutedFg)

            if (pinjam.riwayat.isEmpty()) {
                KartuClay {
                    Text(Teks["belum_riwayat"], fontSize = 12.sp, color = MutedFg)
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(pinjam.riwayat) { item ->
                    val isPinjam = item.tipe == "PINJAM"
                    KartuClay(kecil = true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isPinjam) Merah.copy(alpha = 0.1f) else Hijau.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(if (isPinjam) "+" else "−", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isPinjam) Merah else Hijau)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.tipe, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(item.catatan ?: "", fontSize = 10.sp, color = MutedFg, maxLines = 1)
                            }
                            Text("${item.jumlah} ${Teks["menit"]}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isPinjam) Merah else Hijau)
                        }
                    }
                }
            }
        }
    }
}
