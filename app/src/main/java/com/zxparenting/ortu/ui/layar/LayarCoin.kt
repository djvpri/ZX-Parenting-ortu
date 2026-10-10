package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.CoinRes
import com.zxparenting.ortu.ui.tema.*
import androidx.compose.ui.res.stringResource

@Composable
fun LayarCoin(
    coin: CoinRes?,
    loading: Boolean,
    onTopup: (Int) -> Unit,
) {
    var tampilForm by remember { mutableStateOf(false) }
    var jumlah by remember { mutableStateOf("50") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text("ZX Coin", style = MaterialTheme.typography.titleLarge)

        // Kartu saldo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(Hijau, Biru))),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.saldo), color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                Text(
                    "${coin?.saldo ?: 0}",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text("ZX Coin", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }

        // Tombol topup
        Button(
            onClick = { tampilForm = !tampilForm },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(14.dp),
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Top Up Coin")
        }

        if (tampilForm) {
            KartuClay {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Top Up Manual", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Catatan: topup manual untuk testing. Saat rilis Play Store, Coin dibeli via Play Billing.", fontSize = 10.sp, color = MutedFg)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = jumlah,
                            onValueChange = { jumlah = it.filter { c -> c.isDigit() } },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.zx), fontWeight = FontWeight.Bold, color = Hijau)
                    }
                    // Quick preset
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(50, 100, 500).forEach { v ->
                            OutlinedButton(
                                onClick = { jumlah = v.toString() },
                                shape = RoundedCornerShape(10.dp),
                            ) { Text("$v", fontSize = 11.sp) }
                        }
                    }
                    Button(
                        onClick = {
                            val j = jumlah.toIntOrNull() ?: 0
                            if (j > 0) {
                                onTopup(j)
                                tampilForm = false
                            }
                        },
                        enabled = !loading && (jumlah.toIntOrNull() ?: 0) > 0,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        if (loading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Konfirmasi Top Up")
                        }
                    }
                }
            }
        }

        // Riwayat
        Text(stringResource(R.string.riwayat), fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
        val ledger = coin?.ledger ?: emptyList()
        if (ledger.isEmpty()) {
            KartuClay { Text("Belum ada transaksi.", fontSize = 12.sp, color = MutedFg) }
        }
        ledger.take(20).forEach { e ->
            KartuClay(kecil = true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background((if (e.jumlah > 0) Hijau else Merah).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = if (e.jumlah > 0) Hijau else Merah,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(e.tipe, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        if (!e.catatan.isNullOrBlank()) {
                            Text(e.catatan, fontSize = 10.sp, color = MutedFg)
                        }
                        Text(e.createdAt.take(10), fontSize = 9.sp, color = MutedFg)
                    }
                    Text(
                        "${if (e.jumlah > 0) "+" else ""}${e.jumlah}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (e.jumlah > 0) Hijau else Merah,
                    )
                }
            }
        }
    }
}
