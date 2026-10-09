package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.Anak
import com.zxparenting.ortu.api.Tugas
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarTugas(
    tugasList: List<Tugas>,
    anakList: List<Anak>,
    loading: Boolean,
    onBuat: (anakId: String, judul: String, deskripsi: String?, tokenReward: Int) -> Unit,
    onValidasi: (tugasId: String, aksi: String) -> Unit,
    onJadwalAi: () -> Unit = {},
    onChallenges: () -> Unit = {},
) {
    var tampilForm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Tugas", style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onChallenges) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Misi")
                }
                TextButton(onClick = onJadwalAi) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Jadwal AI")
                }
                TextButton(onClick = { tampilForm = !tampilForm }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Buat")
                }
            }
        }

        if (tampilForm && anakList.isNotEmpty()) {
            FormTugas(anakList = anakList, loading = loading) { a, j, d, r ->
                onBuat(a, j, d, r)
                tampilForm = false
            }
        }
        if (anakList.isEmpty()) {
            KartuClay { Text("Tambah anak dulu sebelum buat tugas.", fontSize = 12.sp, color = MutedFg) }
        }

        // Tugas menunggu validasi di atas
        val menunggu = tugasList.filter { it.status == "MENUNGGU" || it.status == "PENDING" }
        val lainnya = tugasList.filter { it.status != "MENUNGGU" && it.status != "PENDING" }

        if (menunggu.isNotEmpty()) {
            Text("Perlu Validasi", fontSize = 11.sp, color = Amber, fontWeight = FontWeight.Bold)
            menunggu.forEach { KartuTugas(it, onValidasi) }
        }

        if (lainnya.isNotEmpty()) {
            Text("Riwayat", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
            lainnya.forEach { KartuTugas(it, onValidasi) }
        }

        if (tugasList.isEmpty()) {
            KartuClay { Text("Belum ada tugas.", fontSize = 12.sp, color = MutedFg) }
        }
    }
}

@Composable
private fun KartuTugas(tugas: Tugas, onValidasi: (String, String) -> Unit) {
    val warnaStatus = when (tugas.status) {
        "SELESAI" -> Hijau
        "DITOLAK" -> Merah
        "MENUNGGU", "PENDING" -> Amber
        else -> MutedFg
    }
    val teksStatus = when (tugas.status) {
        "SELESAI" -> "Selesai"
        "DITOLAK" -> "Ditolak"
        "MENUNGGU" -> "Menunggu"
        "PENDING" -> "Menunggu"
        else -> tugas.status
    }

    KartuClay(kecil = true) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Biru.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = Biru, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(tugas.judul, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(tugas.anak.nama, fontSize = 10.sp, color = MutedFg)
                }
                BadgePill(teks = teksStatus, bg = warnaStatus.copy(alpha = 0.15f), fg = warnaStatus)
            }

            if (!tugas.deskripsi.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(tugas.deskripsi, fontSize = 11.sp, color = MutedFg)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("+${tugas.tokenReward} token", fontSize = 11.sp, color = Hijau, fontWeight = FontWeight.Bold)
                // Tombol validasi hanya untuk tugas menunggu
                if (tugas.status == "MENUNGGU" || tugas.status == "PENDING") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { onValidasi(tugas.id, "selesai") },
                            modifier = Modifier.size(32.dp),
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Selesai", tint = Hijau)
                        }
                        IconButton(
                            onClick = { onValidasi(tugas.id, "tolak") },
                            modifier = Modifier.size(32.dp),
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Tolak", tint = Merah)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormTugas(
    anakList: List<Anak>,
    loading: Boolean,
    onBuat: (anakId: String, judul: String, deskripsi: String?, tokenReward: Int) -> Unit,
) {
    var anakTerpilih by remember { mutableStateOf(0) }
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var reward by remember { mutableStateOf("1") }
    var dropdown by remember { mutableStateOf(false) }

    KartuClay {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Buat Tugas", fontSize = 14.sp, fontWeight = FontWeight.Bold)

            // Dropdown anak
            Box {
                OutlinedButton(
                    onClick = { dropdown = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(if (anakList.isEmpty()) "Pilih anak" else anakList[anakTerpilih].nama)
                }
                DropdownMenu(expanded = dropdown, onDismissRequest = { dropdown = false }) {
                    anakList.forEachIndexed { i, a ->
                        DropdownMenuItem(text = { Text(a.nama) }, onClick = { anakTerpilih = i; dropdown = false })
                    }
                }
            }

            FieldBiasa("Judul", judul) { judul = it }
            FieldBiasa("Deskripsi (opsional)", deskripsi) { deskripsi = it }
            FieldBiasa("Token reward (1-100)", reward, KeyboardType.Number) { reward = it }

            Button(
                onClick = {
                    val r = reward.toIntOrNull() ?: 1
                    if (anakList.isNotEmpty() && judul.isNotBlank()) {
                        onBuat(anakList[anakTerpilih].id, judul.trim(), deskripsi.ifBlank { null }, r.coerceIn(1, 100))
                    }
                },
                enabled = !loading && judul.isNotBlank() && anakList.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Simpan")
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
