package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.zxparenting.ortu.api.Hadiah
import com.zxparenting.ortu.api.HadiahMarketRes
import com.zxparenting.ortu.api.PesananHadiah
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarMarketplace(
    market: HadiahMarketRes?,
    coinSaldo: Int,
    loading: Boolean,
    onBuatHadiah: (judul: String, deskripsi: String?, hargaCoin: Int, stok: Int) -> Unit,
    onHapusHadiah: (id: String) -> Unit,
    onProsesPesanan: (id: String, aksi: String) -> Unit,
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
            Text("Marketplace", style = MaterialTheme.typography.titleLarge)
            BadgePill(teks = "$coinSaldo ZX", bg = Color(0xFFDCFCE7), fg = Color(0xFF166534))
        }

        // Pesanan masuk
        val pesanan = market?.pesanan ?: emptyList()
        if (pesanan.isNotEmpty()) {
            Text("Pesanan Masuk", fontSize = 11.sp, color = Amber, fontWeight = FontWeight.Bold)
            pesanan.forEach { p ->
                KartuPesanan(p, onProsesPesanan)
            }
        }

        // Daftar hadiah
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Hadiah", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
            TextButton(onClick = { tampilForm = !tampilForm }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Tambah")
            }
        }

        if (tampilForm) {
            FormHadiah(loading = loading) { j, d, h, s ->
                onBuatHadiah(j, d, h, s)
                tampilForm = false
            }
        }

        val hadiah = market?.hadiah ?: emptyList()
        if (hadiah.isEmpty()) {
            KartuClay { Text("Belum ada hadiah.", fontSize = 12.sp, color = MutedFg) }
        }
        hadiah.forEach { h ->
            KartuHadiah(h, onHapusHadiah)
        }
    }
}

@Composable
private fun KartuPesanan(p: PesananHadiah, onProses: (String, String) -> Unit) {
    KartuClay(kecil = true) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Amber.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Amber, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(p.hadiah.judul, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("${p.anak.nama} • ${p.hadiah.hargaCoin} ZX", fontSize = 10.sp, color = MutedFg)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onProses(p.id, "beli") },
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                ) { Text("Beli", fontSize = 12.sp) }
                OutlinedButton(
                    onClick = { onProses(p.id, "tolak") },
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                ) { Text("Tolak", fontSize = 12.sp, color = Merah) }
            }
        }
    }
}

@Composable
private fun KartuHadiah(h: Hadiah, onHapus: (String) -> Unit) {
    KartuClay(kecil = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Pink.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Pink, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(h.judul, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                if (!h.deskripsi.isNullOrBlank()) {
                    Text(h.deskripsi, fontSize = 10.sp, color = MutedFg)
                }
                Text("Stok: ${if (h.stok == -1) "∞" else h.stok}", fontSize = 10.sp, color = MutedFg)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${h.hargaCoin}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Hijau)
                Text("ZX", fontSize = 9.sp, color = MutedFg)
            }
            IconButton(onClick = { onHapus(h.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Merah, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun FormHadiah(
    loading: Boolean,
    onBuat: (judul: String, deskripsi: String?, hargaCoin: Int, stok: Int) -> Unit,
) {
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var harga by remember { mutableStateOf("10") }
    var stok by remember { mutableStateOf("1") }

    KartuClay {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Buat Hadiah", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            FieldBiasa("Judul", judul) { judul = it }
            FieldBiasa("Deskripsi (opsional)", deskripsi) { deskripsi = it }
            FieldBiasa("Harga (ZX Coin)", harga, KeyboardType.Number) { harga = it }
            FieldBiasa("Stok (-1 = tanpa batas)", stok, KeyboardType.Number) { stok = it }
            Button(
                onClick = {
                    val h = harga.toIntOrNull() ?: 0
                    val s = stok.toIntOrNull() ?: 1
                    if (judul.isNotBlank() && h >= 0) {
                        onBuat(judul.trim(), deskripsi.ifBlank { null }, h, s)
                    }
                },
                enabled = !loading && judul.isNotBlank(),
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
