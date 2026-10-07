package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.LeaderboardRes
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarLeaderboard(
    leaderboard: LeaderboardRes?,
    scope: String,
    onScope: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Bg).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text(Teks["leaderboard"], style = MaterialTheme.typography.titleLarge)

        // Scope toggle
        KartuClay(kecil = true) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = scope == "global",
                    onClick = { onScope("global") },
                    label = { Text(Teks["global"]) },
                    modifier = Modifier.weight(1f),
                )
                FilterChip(
                    selected = scope == "kelas",
                    onClick = { onScope("kelas") },
                    label = { Text(Teks["kelas"]) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        if (leaderboard != null) {
            Text("${Teks["periode"]}: ${leaderboard.periode}", fontSize = 11.sp, color = MutedFg)

            if (leaderboard.ranking.isEmpty()) {
                KartuClay {
                    Text(Teks["belum_riwayat"], fontSize = 12.sp, color = MutedFg)
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(leaderboard.ranking) { entry ->
                    val medalColor = when (entry.rank) {
                        1 -> Amber
                        2 -> MutedFg
                        3 -> Merah
                        else -> Biru
                    }
                    KartuClay(kecil = true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(medalColor.copy(alpha = if (entry.isMine) 0.2f else 0.1f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (entry.rank <= 3) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = medalColor, modifier = Modifier.size(18.dp))
                                } else {
                                    Text("${entry.rank}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = medalColor)
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    entry.nama + if (entry.isMine) " ★" else "",
                                    fontSize = 13.sp,
                                    fontWeight = if (entry.isMine) FontWeight.Bold else FontWeight.Normal,
                                )
                                Text(entry.kelas ?: "-", fontSize = 10.sp, color = MutedFg)
                            }
                            Text("${entry.tokenMinggu} ${Teks["menit"]}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (entry.isMine) Ungu else MutedFg)
                        }
                    }
                }
            }
        }
    }
}
