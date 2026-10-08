package com.zxparenting.ortu.ui.layar

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.Anak
import com.zxparenting.ortu.ui.tema.*
import java.util.Calendar

private val GENDER_OPSI = listOf("Laki-laki", "Perempuan")

@Composable
fun LayarAnak(
    anakList: List<Anak>,
    loading: Boolean,
    onBuat: (nama: String, username: String, pin: String, tanggalLahir: String?, kelas: String?, gender: String?, agama: String?) -> Unit,
    onEdit: (id: String, nama: String, tanggalLahir: String?, kelas: String?, gender: String?, agama: String?) -> Unit,
    onHapus: (id: String) -> Unit,
) {
    var tampilForm by remember { mutableStateOf(false) }
    var editAnak by remember { mutableStateOf<Anak?>(null) }
    var hapusAnak by remember { mutableStateOf<Anak?>(null) }

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
            Text("Anak", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { tampilForm = !tampilForm }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Tambah")
            }
        }

        if (tampilForm) {
            FormAnak(loading = loading, onBuat = { n, u, p, tl, k, g, a ->
                onBuat(n, u, p, tl, k, g, a)
                tampilForm = false
            })
        }

        if (anakList.isEmpty()) {
            KartuClay {
                Text("Belum ada anak. Klik Tambah untuk membuat profil anak.", fontSize = 12.sp, color = MutedFg)
            }
        }

        anakList.forEach { anak ->
            KartuAnak(
                anak = anak,
                onEdit = { editAnak = anak },
                onHapus = { hapusAnak = anak },
            )
        }
    }

    editAnak?.let { anak ->
        DialogEditAnak(
            anak = anak,
            loading = loading,
            onBatal = { editAnak = null },
            onSimpan = { nama, tl, k, g, a ->
                onEdit(anak.id, nama, tl, k, g, a)
                editAnak = null
            },
        )
    }

    hapusAnak?.let { anak ->
        AlertDialog(
            onDismissRequest = { hapusAnak = null },
            title = { Text("Hapus Anak") },
            text = {
                Text("Yakin hapus \"${anak.nama}\"? Semua data (tugas, token, riwayat) akan dihapus permanen.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onHapus(anak.id)
                        hapusAnak = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { hapusAnak = null }) { Text("Batal") }
            },
        )
    }
}

@Composable
private fun KartuAnak(anak: Anak, onEdit: () -> Unit, onHapus: () -> Unit) {
    KartuClay(kecil = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Biru.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.ChildCare, contentDescription = null, tint = Biru, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(anak.nama, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("${anak.umur ?: "?"} tahun", fontSize = 11.sp, color = MutedFg)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${anak.tokenBalance?.balance ?: 0}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Hijau,
                )
                Text("Token", fontSize = 9.sp, color = MutedFg)
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp), tint = MutedFg)
            }
            IconButton(onClick = onHapus, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
            }
        }
        if (anak.dormant) {
            Spacer(Modifier.height(8.dp))
            BadgePill(teks = "Dormant", bg = Muted, fg = MutedFg)
        }
    }
}

@Composable
private fun FormAnak(
    loading: Boolean,
    onBuat: (nama: String, username: String, pin: String, tanggalLahir: String?, kelas: String?, gender: String?, agama: String?) -> Unit,
) {
    var nama by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var tanggalLahir by remember { mutableStateOf("") }
    var kelas by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var agama by remember { mutableStateOf("") }

    KartuClay {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Tambah Anak", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            FieldBiasa("Nama", nama) { nama = it }
            FieldBiasa("Username (3-20 huruf/angka)", username) { username = it }
            FieldBiasa("PIN (4-6 digit)", pin, KeyboardType.NumberPassword) { pin = it }
            FieldTanggal("Tanggal Lahir", tanggalLahir) { tanggalLahir = it }
            FieldBiasa("Kelas (opsional)", kelas) { kelas = it }
            FieldDropdown("Gender (opsional)", gender, GENDER_OPSI) { gender = it }
            FieldBiasa("Agama (opsional)", agama) { agama = it }

            Button(
                onClick = {
                    if (nama.isNotBlank() && username.isNotBlank() && pin.isNotBlank()) {
                        onBuat(
                            nama.trim(), username.trim(), pin.trim(),
                            tanggalLahir.ifBlank { null },
                            kelas.ifBlank { null }, gender.ifBlank { null }, agama.ifBlank { null },
                        )
                    }
                },
                enabled = !loading && nama.isNotBlank() && username.isNotBlank() && pin.isNotBlank(),
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
private fun DialogEditAnak(
    anak: Anak,
    loading: Boolean,
    onBatal: () -> Unit,
    onSimpan: (nama: String, tanggalLahir: String?, kelas: String?, gender: String?, agama: String?) -> Unit,
) {
    var nama by remember { mutableStateOf(anak.nama) }
    var tanggalLahir by remember { mutableStateOf(anak.tanggalLahir ?: "") }
    var kelas by remember { mutableStateOf(anak.kelas ?: "") }
    var gender by remember { mutableStateOf(anak.gender ?: "") }
    var agama by remember { mutableStateOf(anak.agama ?: "") }

    AlertDialog(
        onDismissRequest = onBatal,
        title = { Text("Edit Anak") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
                FieldTanggal("Tanggal Lahir", tanggalLahir) { tanggalLahir = it }
                OutlinedTextField(
                    value = kelas,
                    onValueChange = { kelas = it },
                    label = { Text("Kelas") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
                FieldDropdown("Gender", gender, GENDER_OPSI) { gender = it }
                FieldBiasa("Agama", agama) { agama = it }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSimpan(
                        nama.trim(),
                        tanggalLahir.ifBlank { null },
                        kelas.ifBlank { null },
                        gender.ifBlank { null },
                        agama.ifBlank { null },
                    )
                },
                enabled = !loading && nama.isNotBlank(),
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onBatal) { Text("Batal") }
        },
    )
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

@Composable
private fun FieldTanggal(label: String, value: String, onubah: (String) -> Unit) {
    val context = LocalContext.current
    val cal = Calendar.getInstance()

    Column {
        Text(label, fontSize = 10.sp, color = MutedFg, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            readOnly = true,
            enabled = false,
            placeholder = { Text("Pilih tanggal") },
            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp)) },
            shape = RoundedCornerShape(12.dp),
        )
        // ponytail: OutlinedTextField readOnly+enabled=false is not clickable in all Compose versions.
        // Fallback: small clickable Text below field to open picker.
        TextButton(
            onClick = {
                val tahun = if (value.isNotBlank()) value.substring(0, 4).toIntOrNull() ?: cal.get(Calendar.YEAR) else cal.get(Calendar.YEAR)
                val bulan = if (value.length >= 7) (value.substring(5, 7).toIntOrNull() ?: 1) - 1 else cal.get(Calendar.MONTH)
                val hari = if (value.length >= 10) value.substring(8, 10).toIntOrNull() ?: 1 else cal.get(Calendar.DAY_OF_MONTH)
                DatePickerDialog(
                    context,
                    { _, y, m, d ->
                        val mm = (m + 1).toString().padStart(2, '0')
                        val dd = d.toString().padStart(2, '0')
                        onubah("$y-$mm-$dd")
                    },
                    tahun, bulan, hari,
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (value.isBlank()) "Pilih tanggal lahir" else value, fontSize = 12.sp)
        }
    }
}

@Composable
private fun FieldDropdown(
    label: String,
    value: String,
    opsi: List<String>,
    onubah: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontSize = 10.sp, color = MutedFg, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Box {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = true,
                placeholder = { Text("Pilih") },
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    TextButton(onClick = { expanded = true }) {
                        Text("▼", fontSize = 10.sp)
                    }
                },
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                opsi.forEach { opsiItem ->
                    DropdownMenuItem(
                        text = { Text(opsiItem) },
                        onClick = {
                            onubah(opsiItem)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
