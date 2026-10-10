@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.zxparenting.ortu.api.JadwalTugasAI
import com.zxparenting.ortu.ui.tema.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayarJadwalAi(
    jadwalList: List<JadwalTugasAI>,
    anakList: List<Anak>,
    loading: Boolean,
    onBuat: (anakIds: List<String>, tema: String, kesulitan: Int, jamKirim: String, hariAktif: List<Int>, tokenReward: Int, autoApprove: Boolean, maxRetry: Int, timerMenit: Int?) -> Unit,
    onToggle: (id: String, aktif: Boolean) -> Unit,
    onHapus: (id: String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
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
            Text("Jadwal AI", style = MaterialTheme.typography.titleLarge)
            ExtendedFloatingActionButton(
                onClick = { tampilForm = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp)) },
                text = { Text("Buat Jadwal") },
                containerColor = Biru,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }

        if (anakList.isEmpty()) {
            KartuClay { Text("Tambah anak dulu sebelum buat jadwal AI.", fontSize = 12.sp, color = MutedFg) }
        }

        if (jadwalList.isEmpty() && anakList.isNotEmpty()) {
            KartuClay {
                Text(
                    "Belum ada jadwal AI. Buat jadwal untuk generate soal otomatis tiap hari.",
                    fontSize = 12.sp,
                    color = MutedFg,
                )
            }
        }

        jadwalList.forEach { jadwal ->
            KartuJadwalAi(jadwal, anakList, onToggle, onHapus)
        }
    }

    if (tampilForm) {
        ModalBottomSheet(
            onDismissRequest = { tampilForm = false },
            sheetState = sheetState,
            containerColor = Kartu,
        ) {
            FormJadwalAi(
                anakList = anakList,
                loading = loading,
                onBuat = { ids, tema, k, jam, hari, reward, approve, retry, timer ->
                    onBuat(ids, tema, k, jam, hari, reward, approve, retry, timer)
                    scope.launch {
                        sheetState.hide()
                        tampilForm = false
                    }
                },
            )
        }
    }
}

@Composable
private fun KartuJadwalAi(
    jadwal: JadwalTugasAI,
    anakList: List<Anak>,
    onToggle: (String, Boolean) -> Unit,
    onHapus: (String) -> Unit,
) {
    val namaAnak = remember(jadwal.anakIds) {
        try {
            val arr = JSONArray(jadwal.anakIds)
            (0 until arr.length()).map { i ->
                val id = arr.getString(i)
                anakList.find { it.id == id }?.nama ?: "?"
            }.joinToString(", ")
        } catch (_: Exception) { "?" }
    }

    val hariLabel = remember(jadwal.hariAktif) {
        val nama = mapOf(1 to "Sen", 2 to "Sel", 3 to "Rab", 4 to "Kam", 5 to "Jum", 6 to "Sab", 7 to "Min")
        try {
            val arr = JSONArray(jadwal.hariAktif)
            (0 until arr.length()).map { i -> nama[arr.getInt(i)] ?: "?" }.joinToString(" ")
        } catch (_: Exception) { "?" }
    }

    KartuClay {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(jadwal.tema, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("$namaAnak • $hariLabel • ${jadwal.jamKirim}", fontSize = 11.sp, color = MutedFg)
                }
                Switch(
                    checked = jadwal.aktif,
                    onCheckedChange = { onToggle(jadwal.id, it) },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Kesulitan: ${jadwal.kesulitan}/5", fontSize = 11.sp, color = MutedFg)
                Text("Reward: ${jadwal.tokenReward} token", fontSize = 11.sp, color = MutedFg)
                Text("Retry: ${if (jadwal.maxRetry == 0) "∞" else jadwal.maxRetry}/hari", fontSize = 11.sp, color = MutedFg)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = { onHapus(jadwal.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = Merah)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.hapus), color = Merah, fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormJadwalAi(
    anakList: List<Anak>,
    loading: Boolean,
    onBuat: (List<String>, String, Int, String, List<Int>, Int, Boolean, Int, Int?) -> Unit,
) {
    val NAMA_HARI = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
    val ISO_HARI = listOf(1, 2, 3, 4, 5, 6, 7)

    var pilihAnak by remember { mutableStateOf(setOf<String>()) }
    var tema by remember { mutableStateOf("") }
    var kesulitan by remember { mutableFloatStateOf(3f) }
    var jam by remember { mutableStateOf("15:00") }
    var hariAktif by remember { mutableStateOf(setOf(1, 2, 3, 4, 5)) }
    var tokenReward by remember { mutableStateOf("10") }
    var autoApprove by remember { mutableStateOf(false) }
    var maxRetry by remember { mutableStateOf("3") }
    var timerMenit by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Buat Jadwal AI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        // Pilih anak
        Text("Pilih Anak", fontSize = 12.sp, color = MutedFg, fontWeight = FontWeight.Medium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            anakList.forEach { anak ->
                FilterChip(
                    selected = anak.id in pilihAnak,
                    onClick = {
                        pilihAnak = if (anak.id in pilihAnak) pilihAnak - anak.id else pilihAnak + anak.id
                    },
                    label = { Text(anak.nama, fontSize = 12.sp) },
                )
            }
        }

        // Tema
        OutlinedTextField(
            value = tema,
            onValueChange = { tema = it },
            label = { Text("Tema / Mata Pelajaran") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Matematika, Sains, Bahasa...") },
        )

        // Kesulitan slider
        Column {
            Text("Tingkat Kesulitan: ${kesulitan.toInt()}/5", fontSize = 12.sp, color = MutedFg)
            Slider(
                value = kesulitan,
                onValueChange = { kesulitan = it },
                valueRange = 1f..5f,
                steps = 3,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                when (kesulitan.toInt()) {
                    1 -> "Simple recall, single-step"
                    2 -> "Basic application, single operation"
                    3 -> "Multi-step word problems"
                    4 -> "Requires reasoning"
                    5 -> "Complex, advanced reasoning"
                    else -> ""
                },
                fontSize = 10.sp,
                color = MutedFg,
            )
        }

        // Jam kirim
        OutlinedTextField(
            value = jam,
            onValueChange = { jam = it },
            label = { Text("Jam Kirim (HH:mm)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text(stringResource(R.string._15_00)) },
        )

        // Hari aktif
        Text("Hari Aktif", fontSize = 12.sp, color = MutedFg, fontWeight = FontWeight.Medium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            NAMA_HARI.forEachIndexed { i, nama ->
                val iso = ISO_HARI[i]
                FilterChip(
                    selected = iso in hariAktif,
                    onClick = {
                        hariAktif = if (iso in hariAktif) hariAktif - iso else hariAktif + iso
                    },
                    label = { Text(nama, fontSize = 12.sp) },
                )
            }
        }

        // Token reward + max retry
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = tokenReward,
                onValueChange = { tokenReward = it.filter { c -> c.isDigit() } },
                label = { Text("Token Reward") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                value = maxRetry,
                onValueChange = { maxRetry = it.filter { c -> c.isDigit() } },
                label = { Text("Max Retry") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }

        // Timer (opsional)
        OutlinedTextField(
            value = timerMenit,
            onValueChange = { timerMenit = it.filter { c -> c.isDigit() } },
            label = { Text("Timer (menit, kosong = tanpa timer)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        // Auto approve
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Auto-Approve Token", fontSize = 13.sp)
                Text("Token masuk otomatis tanpa validasi ortu", fontSize = 10.sp, color = MutedFg)
            }
            Switch(checked = autoApprove, onCheckedChange = { autoApprove = it })
        }

        Spacer(Modifier.height(4.dp))
        Button(
            onClick = {
                if (pilihAnak.isNotEmpty() && tema.isNotBlank()) {
                    onBuat(
                        pilihAnak.toList(),
                        tema.trim(),
                        kesulitan.toInt(),
                        jam,
                        hariAktif.toList(),
                        tokenReward.toIntOrNull() ?: 10,
                        autoApprove,
                        maxRetry.toIntOrNull() ?: 3,
                        timerMenit.toIntOrNull(),
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading && pilihAnak.isNotEmpty() && tema.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = Biru),
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text("Simpan Jadwal")
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
