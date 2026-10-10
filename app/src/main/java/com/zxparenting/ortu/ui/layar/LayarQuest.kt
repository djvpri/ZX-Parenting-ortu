package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.Quest
import com.zxparenting.ortu.ui.tema.*
import androidx.compose.ui.res.stringResource
import com.zxparenting.ortu.R

@Composable
fun LayarQuest(
    quests: List<Quest>,
    loading: Boolean,
    onBuat: (judul: String, deskripsi: String?, tokenReward: Int) -> Unit,
) {
    var tampilForm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Family Quest", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { tampilForm = !tampilForm }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.buat))
            }
        }

        if (tampilForm) {
            FormQuest(loading = loading) { j, d, r ->
                onBuat(j, d, r)
                tampilForm = false
            }
        }

        if (quests.isEmpty()) {
            KartuClay {
                Text("Belum ada quest. Buat quest keluarga untuk memotivasi anak!", fontSize = 12.sp, color = MutedFg)
            }
        }

        quests.forEach { q ->
            KartuQuest(q)
        }
    }
}

@Composable
private fun KartuQuest(q: Quest) {
    val selesaiCount = q.anggota.count { it.selesai }
    val totalCount = q.anggota.size

    KartuClay(kecil = true) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Ungu.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Flag, contentDescription = null, tint = Ungu, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(q.judul, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    if (!q.deskripsi.isNullOrBlank()) {
                        Text(q.deskripsi, fontSize = 10.sp, color = MutedFg)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("+${q.tokenReward}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Hijau)
                    Text(stringResource(R.string.token), fontSize = 9.sp, color = MutedFg)
                }
            }

            Spacer(Modifier.height(8.dp))
            // Progress anggota
            if (totalCount > 0) {
                Text(
                    "Progres: $selesaiCount/$totalCount selesai",
                    fontSize = 10.sp,
                    color = MutedFg,
                )
                LinearProgressIndicator(
                    progress = { if (totalCount > 0) selesaiCount.toFloat() / totalCount else 0f },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = Ungu,
                )
                Spacer(Modifier.height(4.dp))
                q.anggota.forEach { a ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(a.anak?.nama ?: "?", fontSize = 10.sp, color = MutedFg)
                        Text(if (a.selesai) "✓" else "○", fontSize = 10.sp, color = if (a.selesai) Hijau else MutedFg)
                    }
                }
            }
        }
    }
}

@Composable
private fun FormQuest(
    loading: Boolean,
    onBuat: (judul: String, deskripsi: String?, tokenReward: Int) -> Unit,
) {
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var reward by remember { mutableStateOf("5") }

    KartuClay {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Buat Quest Keluarga", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("Semua anak aktif otomatis jadi anggota.", fontSize = 10.sp, color = MutedFg)
            FieldBiasa(stringResource(R.string.judul), judul) { judul = it }
            FieldBiasa("Deskripsi (opsional)", deskripsi) { deskripsi = it }
            FieldBiasa("Token reward (1-1000)", reward, KeyboardType.Number) { reward = it }
            Button(
                onClick = {
                    val r = reward.toIntOrNull() ?: 1
                    if (judul.isNotBlank()) {
                        onBuat(judul.trim(), deskripsi.ifBlank { null }, r.coerceIn(1, 1000))
                    }
                },
                enabled = !loading && judul.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.simpan))
                }
            }
        }
    }
}

@Composable
private fun FieldBiasa(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onubah: (String) -> Unit,
) {
    Column {
        Text(label, fontSize = 10.sp, color = MutedFg, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onubah,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
        )
    }
}
