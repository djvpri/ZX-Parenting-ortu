package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.Device
import com.zxparenting.ortu.api.DevicePatch
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarAturan(
    devices: List<Device>,
    onPatch: (deviceId: String, patch: DevicePatch) -> Unit,
    onDelete: (deviceId: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text("Aturan Perangkat", style = MaterialTheme.typography.titleLarge)

        if (devices.isEmpty()) {
            KartuClay {
                Text("Belum ada perangkat terhubung.\nPasang app agent di HP anak lalu lakukan pairing.", fontSize = 12.sp, color = MutedFg)
            }
        }

        devices.forEach { dev ->
            KartuAturanDevice(dev, onPatch, onDelete)
        }
    }
}

@Composable
private fun KartuAturanDevice(
    dev: Device,
    onPatch: (String, DevicePatch) -> Unit,
    onDelete: (String) -> Unit,
) {
    var sesi by remember(dev.id) { mutableStateOf(dev.sessionLimit?.toString() ?: "") }
    var cooldown by remember(dev.id) { mutableStateOf(dev.cooldown?.toString() ?: "") }
    var harian by remember(dev.id) { mutableStateOf(dev.dailyLimit?.toString() ?: "") }
    var curfewMulai by remember(dev.id) { mutableStateOf(dev.curfewMulai ?: "") }
    var curfewSelesai by remember(dev.id) { mutableStateOf(dev.curfewSelesai ?: "") }
    var masterUnlock by remember(dev.id) { mutableStateOf(dev.masterUnlock ?: false) }
    var gps by remember(dev.id) { mutableStateOf(dev.gpsAktif ?: false) }
    var appBlokir by remember(dev.id) { mutableStateOf(dev.appBlokir ?: emptyList()) }
    var appBaru by remember(dev.id) { mutableStateOf("") }
    var tanyaHapus by remember { mutableStateOf(false) }

    KartuClay {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Ungu.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Devices, contentDescription = null, tint = Ungu, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(dev.nama, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(dev.anak?.nama ?: "-", fontSize = 11.sp, color = MutedFg)
                }
                IconButton(onClick = { tanyaHapus = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Merah)
                }
            }

            HorizontalDivider(color = Border)

            // Batas waktu
            Text("Batas Waktu", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
            FieldAngka("Sesi limit (menit)", sesi) { sesi = it }
            FieldAngka("Cooldown (menit)", cooldown) { cooldown = it }
            FieldAngka("Limit harian (menit)", harian) { harian = it }

            // Curfew
            Text("Jam Tidur (Curfew)", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldTeks("Mulai (HH:MM)", curfewMulai, Modifier.weight(1f)) { curfewMulai = it }
                FieldTeks("Selesai (HH:MM)", curfewSelesai, Modifier.weight(1f)) { curfewSelesai = it }
            }

            // Toggle GPS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("GPS Pelacakan", fontSize = 12.sp)
                Switch(checked = gps, onCheckedChange = {
                    gps = it
                    onPatch(dev.id, DevicePatch(gpsAktif = it))
                })
            }

            // Master unlock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Master Unlock", fontSize = 12.sp)
                    Text("Buka semua batasan", fontSize = 10.sp, color = MutedFg)
                }
                Switch(checked = masterUnlock, onCheckedChange = {
                    masterUnlock = it
                    onPatch(dev.id, DevicePatch(masterUnlock = it))
                })
            }

            // App blocker
            Text("App Diblokir", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
            Text("Nama paket Android (mis. com.roblox)", fontSize = 9.sp, color = MutedFg)
            appBlokir.forEach { pkg ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(pkg, fontSize = 11.sp, modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        appBlokir = appBlokir.filter { it != pkg }
                        onPatch(dev.id, DevicePatch(appBlokir = appBlokir.filter { it != pkg }))
                    }) { Text("Hapus", fontSize = 10.sp, color = Merah) }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = appBaru,
                    onValueChange = { appBaru = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("com.nama.app", fontSize = 11.sp) },
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (appBaru.isNotBlank() && !appBlokir.contains(appBaru.trim())) {
                            val baru = appBaru.trim()
                            appBlokir = appBlokir + baru
                            onPatch(dev.id, DevicePatch(appBlokir = appBlokir))
                            appBaru = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                ) { Text("+") }
            }

            // Tombol simpan batas waktu
            Button(
                onClick = {
                    val patch = DevicePatch(
                        sessionLimit = sesi.toIntOrNull(),
                        cooldown = cooldown.toIntOrNull(),
                        dailyLimit = harian.toIntOrNull(),
                        curfewMulai = curfewMulai.ifBlank { null },
                        curfewSelesai = curfewSelesai.ifBlank { null },
                    )
                    onPatch(dev.id, patch)
                },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(14.dp),
            ) { Text("Simpan Aturan") }
        }
    }

    if (tanyaHapus) {
        AlertDialog(
            onDismissRequest = { tanyaHapus = false },
            title = { Text("Hapus perangkat?") },
            text = { Text("Pairing ${dev.nama} akan dicabut. HP anak tidak lagi terkontrol.") },
            confirmButton = {
                TextButton(onClick = { tanyaHapus = false; onDelete(dev.id) }) {
                    Text("Hapus", color = Merah)
                }
            },
            dismissButton = { TextButton(onClick = { tanyaHapus = false }) { Text("Batal") } },
        )
    }
}

@Composable
private fun FieldAngka(label: String, value: String, onubah: (String) -> Unit) {
    Column {
        Text(label, fontSize = 10.sp, color = MutedFg)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onubah,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
        )
    }
}

@Composable
private fun FieldTeks(label: String, value: String, modifier: Modifier, onubah: (String) -> Unit) {
    Column(modifier = modifier) {
        Text(label, fontSize = 10.sp, color = MutedFg)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onubah,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
        )
    }
}
