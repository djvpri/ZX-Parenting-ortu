package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChildCare
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
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarAnak(
    anakList: List<Anak>,
    loading: Boolean,
    onBuat: (nama: String, username: String, pin: String, umur: Int, kelas: String?, gender: String?, agama: String?) -> Unit,
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
            Text("Anak", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { tampilForm = !tampilForm }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Tambah")
            }
        }

        if (tampilForm) {
            FormAnak(loading = loading, onBuat = { n, u, p, um, k, g, a ->
                onBuat(n, u, p, um, k, g, a)
                tampilForm = false
            })
        }

        if (anakList.isEmpty()) {
            KartuClay {
                Text("Belum ada anak. Klik Tambah untuk membuat profil anak.", fontSize = 12.sp, color = MutedFg)
            }
        }

        anakList.forEach { anak ->
            KartuAnak(anak)
        }
    }
}

@Composable
private fun KartuAnak(anak: Anak) {
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
    onBuat: (nama: String, username: String, pin: String, umur: Int, kelas: String?, gender: String?, agama: String?) -> Unit,
) {
    var nama by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var umur by remember { mutableStateOf("") }
    var kelas by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var agama by remember { mutableStateOf("") }

    KartuClay {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Tambah Anak", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            FieldBiasa("Nama", nama) { nama = it }
            FieldBiasa("Username (3-20 huruf/angka)", username) { username = it }
            FieldBiasa("PIN (4-6 digit)", pin, KeyboardType.NumberPassword) { pin = it }
            FieldBiasa("Umur (1-25)", umur, KeyboardType.Number) { umur = it }
            FieldBiasa("Kelas (opsional)", kelas) { kelas = it }
            FieldBiasa("Gender (opsional)", gender) { gender = it }
            FieldBiasa("Agama (opsional)", agama) { agama = it }

            Button(
                onClick = {
                    val um = umur.toIntOrNull()
                    if (nama.isNotBlank() && username.isNotBlank() && pin.isNotBlank() && um != null) {
                        onBuat(nama.trim(), username.trim(), pin.trim(), um, kelas.ifBlank { null }, gender.ifBlank { null }, agama.ifBlank { null })
                    }
                },
                enabled = !loading && nama.isNotBlank() && username.isNotBlank() && pin.isNotBlank() && umur.isNotBlank(),
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
