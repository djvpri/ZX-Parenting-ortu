package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.Anak
import com.zxparenting.ortu.api.ChallengesRes
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarChallenges(
    anakList: List<Anak>,
    challenges: ChallengesRes?,
    loading: Boolean,
    hasilMsg: String?,
    onPilihAnak: (String) -> Unit,
    onKlaim: (String) -> Unit,
) {
    var selectedAnak by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(Bg).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text(Teks["daily_challenges"], style = MaterialTheme.typography.titleLarge)

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

        if (loading && challenges == null && selectedAnak != null) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        hasilMsg?.let {
            Text(it, fontSize = 13.sp, color = if (it.startsWith("✓")) Hijau else Merah, modifier = Modifier.padding(4.dp))
        }

        if (challenges != null) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(challenges.challenges) { ch ->
                    KartuClay(kecil = true) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (ch.selesai) Hijau else Amber),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        if (ch.selesai) Icons.Default.CheckCircle else Icons.Default.Star,
                                        contentDescription = null,
                                        tint = androidx.compose.ui.graphics.Color.White,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ch.nama, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(ch.deskripsi, fontSize = 11.sp, color = MutedFg)
                                }
                                Text("+${ch.reward}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Amber)
                            }
                            Spacer(Modifier.height(8.dp))
                            // Progress bar
                            LinearProgressIndicator(
                                progress = if (ch.target > 0) ch.progress.toFloat() / ch.target.toFloat() else 0f,
                                modifier = Modifier.fillMaxWidth(),
                                color = if (ch.selesai) Hijau else Amber,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("${ch.progress}/${ch.target}", fontSize = 10.sp, color = MutedFg)
                                if (ch.diklaim) {
                                    Text(Teks["diklaim"], fontSize = 10.sp, color = MutedFg, fontWeight = FontWeight.Bold)
                                } else if (ch.selesai) {
                                    Button(
                                        onClick = { onKlaim(ch.id) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    ) { Text(Teks["klaim"], fontSize = 11.sp) }
                                } else {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = MutedFg, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
