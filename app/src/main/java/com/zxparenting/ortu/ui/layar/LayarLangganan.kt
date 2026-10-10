package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.LanggananRes
import com.zxparenting.ortu.ui.tema.*
import androidx.compose.ui.res.stringResource

@Composable
fun LayarLangganan(
    langganan: LanggananRes?,
    loading: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text(Teks["langganan"], style = MaterialTheme.typography.titleLarge)

        if (loading && langganan == null) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        if (langganan == null || langganan.tier == null) {
            // Tidak ada langganan aktif
            KartuClay {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(Teks["trial_habis"], fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(Teks["trial_habis"], fontSize = 12.sp, color = MutedFg)
                    Spacer(Modifier.height(16.dp))
                    Text("${Teks["upgrade"]} → ZX ${Teks["tier_pro"]} / ${Teks["tier_elite"]}.", fontSize = 12.sp, color = MutedFg)
                }
            }
            return@Column
        }

        val tierLabel = when (langganan.tier) {
            "FREE_TRIAL" -> "Free Trial (Elite)"
            "PRO" -> "ZX Pro"
            "ELITE" -> "ZX Elite"
            else -> langganan.tier
        }
        val tierWarna = when (langganan.tier) {
            "FREE_TRIAL" -> Amber
            "PRO" -> Biru
            "ELITE" -> Ungu
            else -> Muted
        }

        // Banner status
        KartuClay {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(tierWarna),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(tierLabel, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(if (langganan.aktif) "Aktif" else "Nonaktif", fontSize = 11.sp, color = MutedFg)
                    }
                }
                Spacer(Modifier.height(16.dp))

                if (langganan.tier == "FREE_TRIAL") {
                    // Trial countdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("${Teks["trial_aktif"]}: ${langganan.hariSisa} ${Teks["hari"]}", fontSize = 12.sp, color = MutedFg)
                        Text("${langganan.hariSisa} ${Teks["hari"]}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (langganan.hariSisa <= 7) Merah else Hijau)
                    }
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = (langganan.hariSisa.toFloat() / 30f).coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(99.dp)),
                        color = tierWarna,
                        trackColor = Muted,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Setelah trial berakhir, pilih paket untuk melanjutkan. Anak tambahan akan masuk status Dormant jika turun ke Pro.",
                        fontSize = 11.sp,
                        color = MutedFg,
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(stringResource(R.string.berakhir), fontSize = 12.sp, color = MutedFg)
                        Text("${langganan.hariSisa} ${Teks["hari"]}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Info limit anak
        KartuClay(kecil = true) {
            BarisKV(
                k = Teks["limit_anak"],
                v = if (langganan.limitAnak == Int.MAX_VALUE) "Tak terbatas" else "${langganan.limitAnak}",
            )
        }

        // Paket comparison
        Spacer(Modifier.height(8.dp))
        Text(Teks["paket"], fontWeight = FontWeight.Bold, fontSize = 15.sp)

        KartuPaket(
            nama = "ZX Pro",
            harga = "Rp100.000/bulan",
            fitur = listOf("1 profil anak", "Kontrol penuh", "Preset & kuis tanpa batas", "Pelacakan GPS"),
            warna = Biru,
        )
        KartuPaket(
            nama = "ZX Elite",
            harga = "Rp250.000/bulan",
            fitur = listOf("Profil anak tak terbatas", "Akses kreator marketplace", "Fitur prioritas", "Analitik tingkat lanjut"),
            warna = Ungu,
        )
    }
}

@Composable
private fun KartuPaket(
    nama: String,
    harga: String,
    fitur: List<String>,
    warna: Color,
) {
    KartuClay(kecil = true) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(nama, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = warna)
                Text(harga, fontSize = 12.sp, color = MutedFg)
            }
            Spacer(Modifier.height(8.dp))
            fitur.forEach { f ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("•", fontSize = 12.sp, color = warna, modifier = Modifier.width(16.dp))
                    Text(f, fontSize = 12.sp)
                }
            }
        }
    }
}
