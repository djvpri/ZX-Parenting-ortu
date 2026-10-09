package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.Anak
import com.zxparenting.ortu.api.Tugas
import com.zxparenting.ortu.api.TugasSaran
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarTugas(
    tugasList: List<Tugas>,
    anakList: List<Anak>,
    loading: Boolean,
    saranTugas: List<TugasSaran>,
    generateLoading: Boolean,
    jwt: String?,
    onBuat: (anakId: String, judul: String, deskripsi: String?, tokenReward: Int) -> Unit,
    onValidasi: (tugasId: String, aksi: String) -> Unit,
    onGenerate: (anakId: String, kategori: String, jumlah: Int) -> Unit,
    onBatch: (anakId: String, tugas: List<TugasSaran>) -> Unit,
    onJadwalAi: () -> Unit = {},
    onChallenges: () -> Unit = {},
) {
    var tampilForm by remember { mutableStateOf(false) }
    var tampilGenerate by remember { mutableStateOf(false) }
    var tugasDipilih by remember { mutableStateOf<Tugas?>(null) }
    // Pre-fill form dari "Buat Ulang"
    var prefillJudul by remember { mutableStateOf<String?>(null) }
    var prefillDeskripsi by remember { mutableStateOf<String?>(null) }
    var prefillToken by remember { mutableStateOf(1) }

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
                TextButton(onClick = { tampilGenerate = !tampilGenerate }) {
                    Text("Generate")
                }
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
            FormTugas(
                anakList = anakList,
                loading = loading,
                judulInit = prefillJudul,
                deskripsiInit = prefillDeskripsi,
                tokenInit = prefillToken,
            ) { a, j, d, r ->
                onBuat(a, j, d, r)
                tampilForm = false
                prefillJudul = null
                prefillDeskripsi = null
            }
        }
        if (tampilGenerate && anakList.isNotEmpty()) {
            PanelGenerate(
                anakList = anakList,
                saran = saranTugas,
                loading = generateLoading,
                onGenerate = onGenerate,
                onBatch = { anakId, terpilih ->
                    onBatch(anakId, terpilih)
                    tampilGenerate = false
                },
            )
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
            // Statistik ringkas
            val selesai = lainnya.count { it.status == "SELESAI" }
            val ditolak = lainnya.count { it.status == "DITOLAK" }
            val expired = lainnya.count { it.status == "EXPIRED" }
            val totalToken = lainnya.filter { it.status == "SELESAI" }.sumOf { it.tokenReward }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                StatChip("Selesai", "$selesai", Hijau, Modifier.weight(1f))
                StatChip("Ditolak", "$ditolak", Merah, Modifier.weight(1f))
                StatChip("Expired", "$expired", MutedFg, Modifier.weight(1f))
                StatChip("Token", "+$totalToken", Amber, Modifier.weight(1f))
            }
            Text("Riwayat", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
            lainnya.forEach { KartuTugas(it, onValidasi, onClick = { tugasDipilih = it }) }
        }

        if (tugasList.isEmpty()) {
            KartuClay { Text("Belum ada tugas.", fontSize = 12.sp, color = MutedFg) }
        }
    }

    // Detail riwayat bottom sheet
    tugasDipilih?.let { tugas ->
        DetailRiwayatSheet(
            tugas = tugas,
            jwt = jwt,
            onDismiss = { tugasDipilih = null },
            onBuatUlang = { t ->
                prefillJudul = t.judul
                prefillDeskripsi = t.deskripsi
                prefillToken = t.tokenReward
                tampilForm = true
                tugasDipilih = null
            },
        )
    }
}

@Composable
private fun KartuTugas(
    tugas: Tugas,
    onValidasi: (String, String) -> Unit,
    onClick: (() -> Unit)? = null,
) {
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

    val isAi = tugas.type == "ai_quiz" || tugas.type == "ai_scheduled"

    KartuClay(kecil = true) {
        Column(
            modifier = if (onClick != null) Modifier.clip(RoundedCornerShape(12.dp)).clickable { onClick() } else Modifier
        ) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(tugas.anak.nama, fontSize = 10.sp, color = MutedFg)
                        if (isAi) {
                            Spacer(Modifier.width(4.dp))
                            Text("AI", fontSize = 9.sp, color = Ungu, fontWeight = FontWeight.Bold)
                        }
                    }
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
    judulInit: String? = null,
    deskripsiInit: String? = null,
    tokenInit: Int = 1,
    onBuat: (anakId: String, judul: String, deskripsi: String?, tokenReward: Int) -> Unit,
) {
    var anakTerpilih by remember { mutableStateOf(0) }
    var judul by remember(judulInit) { mutableStateOf(judulInit ?: "") }
    var deskripsi by remember(deskripsiInit) { mutableStateOf(deskripsiInit ?: "") }
    var reward by remember(tokenInit) { mutableStateOf(tokenInit.toString()) }
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

private val KATEGORI_TUGAS = listOf("Belajar", "Rumah", "Olahraga", "Kreatif", "Akhlak", "Umum")

@Composable
private fun PanelGenerate(
    anakList: List<Anak>,
    saran: List<TugasSaran>,
    loading: Boolean,
    onGenerate: (anakId: String, kategori: String, jumlah: Int) -> Unit,
    onBatch: (anakId: String, tugas: List<TugasSaran>) -> Unit,
) {
    var anakTerpilih by remember { mutableStateOf(0) }
    var kategoriIdx by remember { mutableStateOf(0) }
    var jumlah by remember { mutableStateOf("3") }
    var dropdownAnak by remember { mutableStateOf(false) }
    var dropdownKat by remember { mutableStateOf(false) }
    val terpilih = remember(saran) { mutableStateMapOf<Int, Boolean>() }

    KartuClay {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Ungu, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Generate Tugas AI", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            // Dropdown anak
            Box {
                OutlinedButton(
                    onClick = { dropdownAnak = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(anakList[anakTerpilih].nama)
                }
                DropdownMenu(expanded = dropdownAnak, onDismissRequest = { dropdownAnak = false }) {
                    anakList.forEachIndexed { i, a ->
                        DropdownMenuItem(text = { Text(a.nama) }, onClick = { anakTerpilih = i; dropdownAnak = false })
                    }
                }
            }

            // Dropdown kategori
            Box {
                OutlinedButton(
                    onClick = { dropdownKat = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(KATEGORI_TUGAS[kategoriIdx])
                }
                DropdownMenu(expanded = dropdownKat, onDismissRequest = { dropdownKat = false }) {
                    KATEGORI_TUGAS.forEachIndexed { i, k ->
                        DropdownMenuItem(text = { Text(k) }, onClick = { kategoriIdx = i; dropdownKat = false })
                    }
                }
            }

            FieldBiasa("Jumlah (1-10)", jumlah, KeyboardType.Number) { jumlah = it }

            Button(
                onClick = {
                    val n = (jumlah.toIntOrNull() ?: 3).coerceIn(1, 10)
                    onGenerate(anakList[anakTerpilih].id, KATEGORI_TUGAS[kategoriIdx], n)
                },
                enabled = !loading && anakList.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Generate Saran")
                }
            }

            // Preview saran
            if (saran.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text("Pilih tugas untuk dibuat:", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
                saran.forEachIndexed { i, s ->
                    KartuSaran(
                        saran = s,
                        dipilih = terpilih[i] ?: true,
                        onToggle = { terpilih[i] = !(terpilih[i] ?: true) },
                    )
                }

                val terpilihList = saran.filterIndexed { i, _ -> terpilih[i] ?: true }
                Button(
                    onClick = { onBatch(anakList[anakTerpilih].id, terpilihList) },
                    enabled = terpilihList.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text("Buat ${terpilihList.size} Tugas Terpilih")
                }
            }
        }
    }
}

@Composable
private fun KartuSaran(
    saran: TugasSaran,
    dipilih: Boolean,
    onToggle: () -> Unit,
) {
    KartuClay(kecil = true) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Checkbox(checked = dipilih, onCheckedChange = { onToggle() })
            Column(modifier = Modifier.weight(1f)) {
                Text(saran.judul, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(saran.deskripsi, fontSize = 11.sp, color = MutedFg)
                Text("+${saran.tokenReward} token", fontSize = 11.sp, color = Hijau, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, warna: Color, modifier: Modifier = Modifier) {
    KartuClay(kecil = true, modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = warna)
            Text(label, fontSize = 9.sp, color = MutedFg)
        }
    }
}
