package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.ReferralRes
import com.zxparenting.ortu.ui.tema.*
import androidx.compose.ui.res.stringResource
import com.zxparenting.ortu.R

@Composable
fun LayarReferral(
    referral: ReferralRes?,
    onKlaim: (String) -> Unit,
) {
    var kodeInput by remember { mutableStateOf("") }
    var hasilMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(Bg).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text(Teks["referral"], style = MaterialTheme.typography.titleLarge)

        // Kode referral ortu
        KartuClay {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Ungu),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(Teks["kode_referral"], fontSize = 11.sp, color = MutedFg)
                    Text(referral?.kode ?: "---", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ungu)
                }
            }
        }

        // Statistik
        if (referral != null) {
            KartuClay(kecil = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column { Text(Teks["total_referral"], fontSize = 11.sp, color = MutedFg); Text("${referral.totalReferral}", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    Column { Text(Teks["bonus_coin"], fontSize = 11.sp, color = MutedFg); Text("${referral.totalBonusCoin}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Hijau) }
                }
            }
        }

        // Klaim kode teman
        KartuClay {
            Column {
                Text(Teks["klaim_referral"], fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = kodeInput,
                    onValueChange = { kodeInput = it.uppercase() },
                    placeholder = { Text(stringResource(R.string.abcd1234), fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { onKlaim(kodeInput) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(Teks["klaim"]) }
            }
        }

        hasilMsg?.let {
            Text(it, fontSize = 13.sp, color = if (it.startsWith("Berhasil")) Hijau else Merah, modifier = Modifier.padding(4.dp))
        }

        // Riwayat
        if (referral != null && referral.riwayat.isNotEmpty()) {
            Text(Teks["riwayat_pinjam"], fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MutedFg)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(referral.riwayat) { item ->
                    KartuClay(kecil = true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Hijau, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.catatan ?: "", fontSize = 12.sp, maxLines = 1)
                                Text(item.createdAt.take(10), fontSize = 10.sp, color = MutedFg)
                            }
                            Text("+${item.jumlah}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Hijau)
                        }
                    }
                }
            }
        }
    }
}
