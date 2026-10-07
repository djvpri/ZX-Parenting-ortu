package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.Anak
import com.zxparenting.ortu.api.InsightsRes
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarInsights(
    anakList: List<Anak>,
    insights: InsightsRes?,
    loading: Boolean,
    onPilihAnak: (String) -> Unit,
) {
    var selectedAnak by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(Bg).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text(Teks["insights"], style = MaterialTheme.typography.titleLarge)

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

        if (loading && insights == null && selectedAnak != null) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        if (insights != null) {
            // Sumber badge
            if (insights.sumber != null) {
                KartuClay(kecil = true) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(Teks["sumber_ai"], fontSize = 11.sp, color = MutedFg)
                        Spacer(Modifier.width(8.dp))
                        Text(if (insights.sumber == "gemini") "✨ Gemini AI" else "📊 Rule-based", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (insights.sumber == "gemini") Ungu else MutedFg)
                    }
                }
            }

            // Ringkasan
            KartuClay(kecil = true) {
                Column {
                    Text("${Teks["ringkasan"]} (${insights.periode})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    BarisKV(Teks["total_aktivitas"], "${insights.ringkasan.totalAktivitas}")
                    BarisKV(Teks["tugas_selesai"], "${insights.ringkasan.tugasSelesai}")
                    BarisKV(Teks["tugas_pending"], "${insights.ringkasan.tugasPending}")
                    BarisKV(Teks["saldo_token"], "${insights.ringkasan.saldoToken} ${Teks["menit"]}")
                }
            }

            // Insights
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(insights.insights) { ins ->
                    val (ikon, warna) = when (ins.level) {
                        "warning" -> Icons.Default.Warning to Merah
                        "positive" -> Icons.Default.CheckCircle to Hijau
                        else -> Icons.Default.Info to Biru
                    }
                    KartuClay(kecil = true) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(warna.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(ikon, contentDescription = null, tint = warna, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(ins.kategori, fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
                                Text(ins.teks, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
