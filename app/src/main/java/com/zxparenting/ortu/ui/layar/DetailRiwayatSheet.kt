package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.Klien
import com.zxparenting.ortu.api.ReviewItem
import com.zxparenting.ortu.api.Tugas
import com.zxparenting.ortu.api.TugasReviewRes
import com.zxparenting.ortu.ui.tema.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.res.stringResource
import com.zxparenting.ortu.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailRiwayatSheet(
    tugas: Tugas,
    jwt: String?,
    onDismiss: () -> Unit,
    onBuatUlang: (Tugas) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var review by remember { mutableStateOf<TugasReviewRes?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val isAi = tugas.type == "ai_quiz" || tugas.type == "ai_scheduled"

    LaunchedEffect(tugas.id) {
        if (isAi) {
            scope.launch {
                try {
                    val res = withContext(Dispatchers.IO) {
                        Klien.api.tugasReview("Bearer ${jwt ?: ""}", tugas.id)
                    }
                    if (res.isSuccessful) {
                        review = res.body()
                    } else {
                        error = "Gagal memuat: ${res.code()}"
                    }
                } catch (e: Exception) {
                    error = e.message ?: "Error jaringan"
                }
                loading = false
            }
        } else {
            loading = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Kartu,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                            .background(Biru.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (isAi) Icons.Default.AutoAwesome else Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Biru,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tugas.judul, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(tugas.anak.nama, fontSize = 11.sp, color = MutedFg)
                    }
                }
            }

            // Info grid
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    InfoChip("Status", tugas.status, Modifier.weight(1f))
                    InfoChip(stringResource(R.string.token), "+${tugas.tokenReward}", Modifier.weight(1f))
                    if (tugas.tokenDiklaim != null && tugas.tokenDiklaim > 0) {
                        InfoChip("Diklaim", "${tugas.tokenDiklaim}", Modifier.weight(1f))
                    }
                }
            }

            // Deskripsi
            if (!tugas.deskripsi.isNullOrBlank()) {
                item {
                    KartuClay(kecil = true) {
                        Text(tugas.deskripsi, fontSize = 12.sp, color = MutedFg)
                    }
                }
            }

            // AI Review section
            if (isAi && loading) {
                item {
                    KartuClay(kecil = true) {
                        Text("Memuat hasil jawaban...", fontSize = 12.sp, color = MutedFg)
                    }
                }
            }

            if (isAi && !loading && review != null && review!!.adaReview) {
                item {
                    KartuClay {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Hasil Kuis", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(Modifier.weight(1f))
                                Text(
                                    "Skor: ${review!!.skor ?: 0}/100",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if ((review!!.skor ?: 0) >= 70) Hijau else if ((review!!.skor ?: 0) >= 50) Amber else Merah,
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("${review!!.benarCount ?: 0}/${review!!.totalSoal ?: 0} benar", fontSize = 11.sp, color = MutedFg)
                                Text("•", fontSize = 11.sp, color = MutedFg)
                                Text("${review!!.durasiDetik ?: 0} detik", fontSize = 11.sp, color = MutedFg)
                                Text("•", fontSize = 11.sp, color = MutedFg)
                                Text("${review!!.totalAttempt ?: 1}x percobaan", fontSize = 11.sp, color = MutedFg)
                            }
                        }
                    }
                }

                // Review items
                items(review!!.review ?: emptyList()) { item ->
                    KartuSoalReview(item)
                }
            }

            if (isAi && !loading && review != null && !review!!.adaReview) {
                item {
                    KartuClay(kecil = true) {
                        Text("Anak belum mengerjakan kuis ini.", fontSize = 12.sp, color = MutedFg)
                    }
                }
            }

            if (isAi && !loading && error != null) {
                item {
                    KartuClay(kecil = true) {
                        Text(error!!, fontSize = 12.sp, color = Merah)
                    }
                }
            }

            // Quick actions
            item {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = { onBuatUlang(tugas) }) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Buat Ulang", fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String, modifier: Modifier = Modifier) {
    KartuClay(kecil = true, modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, fontSize = 9.sp, color = MutedFg)
        }
    }
}

@Composable
private fun KartuSoalReview(item: ReviewItem) {
    KartuClay(kecil = true) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${item.posisi + 1}.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.benar) Hijau else Merah,
                )
                Spacer(Modifier.width(6.dp))
                Text(item.question, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                Icon(
                    if (item.benar) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (item.benar) Hijau else Merah,
                    modifier = Modifier.size(16.dp),
                )
            }

            item.options.forEachIndexed { idx, opt ->
                val isBenar = idx == item.jawabanBenar
                val isDipilih = idx == item.jawabanAnak
                val bg = when {
                    isBenar -> Hijau.copy(alpha = 0.12f)
                    isDipilih && !isBenar -> Merah.copy(alpha = 0.12f)
                    else -> androidx.compose.ui.graphics.Color.Transparent
                }
                val fg = when {
                    isBenar -> Hijau
                    isDipilih && !isBenar -> Merah
                    else -> MutedFg
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(bg)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(opt, fontSize = 11.sp, color = fg, modifier = Modifier.weight(1f))
                    if (isBenar) Text("✓ Benar", fontSize = 9.sp, color = Hijau)
                    if (isDipilih && !isBenar) Text("Jawaban anak", fontSize = 9.sp, color = Merah)
                }
            }

            if (item.explanation.isNotBlank()) {
                Text("Pembahasan: ${item.explanation}", fontSize = 10.sp, color = MutedFg, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
