package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.zxparenting.ortu.api.MarketItem
import com.zxparenting.ortu.api.PesananHadiah
import com.zxparenting.ortu.ui.tema.*
import androidx.compose.ui.res.stringResource
import com.zxparenting.ortu.R

// Tab 3: Hadiah (kelola hadiah anak) + Market (item app, beli pakai Coin).
@Composable
fun LayarHadiahKelola(
    market: HadiahMarketRes?,
    marketItems: List<MarketItem>,
    marketLoading: Boolean,
    sudahBeli: Set<String>,
    coinSaldo: Int,
    loading: Boolean,
    onBuatHadiah: (judul: String, deskripsi: String?, hargaToken: Int, stok: Int) -> Unit,
    onHapusHadiah: (id: String) -> Unit,
    onProsesPesanan: (id: String, aksi: String) -> Unit,
    onBeliMarket: (kontenId: String) -> Unit,
    onMuatMarket: () -> Unit,
) {
    var segmen by remember { mutableIntStateOf(0) } // 0=Hadiah Anak, 1=Market

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
            Text(stringResource(R.string.marketplace), style = MaterialTheme.typography.titleLarge)
            BadgePill(teks = "$coinSaldo Coin", bg = Color(0xFFDCFCE7), fg = Color(0xFF166534))
        }

        // Segmented control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF1F5F9)),
        ) {
            SegmenBtn(stringResource(R.string.tab_hadiah_anak), segmen == 0, Modifier.weight(1f)) { segmen = 0 }
            SegmenBtn(stringResource(R.string.tab_market), segmen == 1, Modifier.weight(1f)) { segmen = 1 }
        }

        if (segmen == 0) {
            SectionHadiahAnak(market, loading, onBuatHadiah, onHapusHadiah, onProsesPesanan)
        } else {
            SectionMarket(marketItems, marketLoading, sudahBeli, onBeliMarket, onMuatMarket)
        }
    }
}

// ================= HADIAH ANAK =================

@Composable
private fun SectionHadiahAnak(
    market: HadiahMarketRes?,
    loading: Boolean,
    onBuatHadiah: (String, String?, Int, Int) -> Unit,
    onHapusHadiah: (String) -> Unit,
    onProsesPesanan: (String, String) -> Unit,
) {
    var tampilForm by remember { mutableStateOf(false) }

    // Pesanan masuk
    val pesanan = market?.pesanan ?: emptyList()
    if (pesanan.isNotEmpty()) {
        Text(stringResource(R.string.pesanan_masuk), fontSize = 11.sp, color = Amber, fontWeight = FontWeight.Bold)
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
        Text(stringResource(R.string.hadiah), fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
        TextButton(onClick = { tampilForm = !tampilForm }) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.tambah))
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
        KartuClay { Text(stringResource(R.string.belum_ada_hadiah), fontSize = 12.sp, color = MutedFg) }
    }
    hadiah.forEach { h ->
        KartuHadiah(h, onHapusHadiah)
    }
}

@Composable
private fun SegmenBtn(label: String, aktif: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (aktif) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = if (aktif) FontWeight.Bold else FontWeight.Normal,
            color = if (aktif) MaterialTheme.colorScheme.onPrimary else MutedFg,
        )
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
                    val saldoAnak = p.anak.tokenBalance?.balance ?: 0
                    Text("${p.anak.nama} • ${p.hadiah.hargaToken} Token (saldo: $saldoAnak)", fontSize = 10.sp, color = MutedFg)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onProses(p.id, "terima") },
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                ) { Text(stringResource(R.string.terima), fontSize = 12.sp) }
                OutlinedButton(
                    onClick = { onProses(p.id, "tolak") },
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                ) { Text(stringResource(R.string.tolak), fontSize = 12.sp, color = Merah) }
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
                Text("${stringResource(R.string.stok)}: ${if (h.stok == -1) "∞" else h.stok}", fontSize = 10.sp, color = MutedFg)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${h.hargaToken}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Hijau)
                Text(stringResource(R.string.token), fontSize = 9.sp, color = MutedFg)
            }
            IconButton(onClick = { onHapus(h.id) }) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.hapus), tint = Merah, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun FormHadiah(
    loading: Boolean,
    onBuat: (judul: String, deskripsi: String?, hargaToken: Int, stok: Int) -> Unit,
) {
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var harga by remember { mutableStateOf("10") }
    var stok by remember { mutableStateOf("1") }

    KartuClay {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.buat_hadiah), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            FieldBiasa(stringResource(R.string.judul), judul) { judul = it }
            FieldBiasa(stringResource(R.string.deskripsi_opsional), deskripsi) { deskripsi = it }
            FieldBiasa(stringResource(R.string.harga_token), harga, KeyboardType.Number) { harga = it }
            FieldBiasa(stringResource(R.string.stok_tanpa_batas), stok, KeyboardType.Number) { stok = it }
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
                    Text(stringResource(R.string.simpan))
                }
            }
        }
    }
}

// ================= MARKET APP =================

@Composable
private fun SectionMarket(
    items: List<MarketItem>,
    loading: Boolean,
    sudahBeli: Set<String>,
    onBeli: (String) -> Unit,
    onMuat: () -> Unit,
) {
    LaunchedEffect(Unit) { onMuat() }

    if (items.isEmpty() && loading) {
        KartuClay {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.memuat), fontSize = 12.sp, color = MutedFg)
            }
        }
        return
    }
    if (items.isEmpty()) {
        KartuClay { Text(stringResource(R.string.market_kosong), fontSize = 12.sp, color = MutedFg) }
        return
    }

    items.forEach { item ->
        KartuMarketItem(item, item.id in sudahBeli, onBeli)
    }
}

@Composable
private fun KartuMarketItem(item: MarketItem, sudahBeli: Boolean, onBeli: (String) -> Unit) {
    KartuClay(kecil = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (item.tipe == "PAKET_SOAL") Color(0xFFDBEAFE) else Color(0xFFFCE7F3)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (item.tipe == "PAKET_SOAL") Icons.Default.Quiz else Icons.Default.TipsAndUpdates,
                    contentDescription = null,
                    tint = if (item.tipe == "PAKET_SOAL") Color(0xFF1D4ED8) else Pink,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.judul, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f, fill = false))
                    if (item.isOfficial) {
                        Spacer(Modifier.width(4.dp))
                        BadgePill(teks = "ZX", bg = Color(0xFFDBEAFE), fg = Color(0xFF1D4ED8))
                    }
                }
                if (!item.deskripsi.isNullOrBlank()) {
                    Text(item.deskripsi, fontSize = 10.sp, color = MutedFg, maxLines = 2)
                }
                Text("${stringResource(R.string.terjual)}: ${item.pembelian}", fontSize = 9.sp, color = MutedFg)
            }
            if (sudahBeli) {
                BadgePill(teks = stringResource(R.string.sudah_dibeli), bg = Color(0xFFDCFCE7), fg = Color(0xFF166534))
            } else {
                Button(
                    onClick = { onBeli(item.id) },
                    enabled = true,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp),
                ) {
                    Text(if (item.hargaCoin == 0) stringResource(R.string.gratis) else "${item.hargaCoin} Coin", fontSize = 11.sp)
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
