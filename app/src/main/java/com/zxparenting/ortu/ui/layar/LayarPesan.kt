package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.Pesan
import com.zxparenting.ortu.api.PesanRingkas
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarPesan(
    pesanList: List<PesanRingkas>,
    pesanThread: List<Pesan>,
    loading: Boolean,
    onBukaThread: (String) -> Unit,
    onKirim: (String, String) -> Unit,
    onBack: () -> Unit,
) {
    var partnerId by remember { mutableStateOf<String?>(null) }
    var inputPesan by remember { mutableStateOf("") }

    if (partnerId == null) {
        // List percakapan
        Column(
            modifier = Modifier.fillMaxSize().background(Bg).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Spacer(Modifier.height(14.dp))
            Text(Teks["pesan"], style = MaterialTheme.typography.titleLarge)

            if (loading && pesanList.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            if (pesanList.isEmpty()) {
                KartuClay {
                    Text(Teks["belum_pesan"], fontSize = 12.sp, color = MutedFg)
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(pesanList) { ringkas ->
                    KartuClay(kecil = true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ringkas.userId.take(8) + "...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(ringkas.lastIsi, fontSize = 11.sp, color = MutedFg, maxLines = 1)
                            }
                            TextButton(onClick = { partnerId = ringkas.userId; onBukaThread(ringkas.userId) }) {
                                Text(Teks["buka"])
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Thread detail
        Column(
            modifier = Modifier.fillMaxSize().background(Bg).padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { partnerId = null; onBack() }) { Text(Teks["kembali"]) }
                Text(Teks["pesan"], fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(pesanThread) { p ->
                    val isMe = p.pengirimId != partnerId
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isMe) Biru else Kartu)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .widthIn(max = 240.dp),
                        ) {
                            Text(p.isi, fontSize = 13.sp, color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // Input
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = inputPesan,
                    onValueChange = { inputPesan = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(Teks["tulis_pesan"], fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (inputPesan.isNotBlank() && partnerId != null) {
                            onKirim(partnerId!!, inputPesan)
                            inputPesan = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                ) { Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp)) }
            }
        }
    }
}
