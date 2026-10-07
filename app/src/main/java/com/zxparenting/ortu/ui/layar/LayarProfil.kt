package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.BuildConfig
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarProfil(
    nama: String?,
    onLogout: () -> Unit,
    onHapusAkun: () -> Unit,
) {
    var tanyaHapus by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text("Profil", style = MaterialTheme.typography.titleLarge)

        KartuClay(kecil = true) {
            Column {
                Text("Nama", fontSize = 10.sp, color = MutedFg, fontWeight = FontWeight.Bold)
                Text(nama ?: "-", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        KartuClay(kecil = true) {
            Text("ZX Parenting v${BuildConfig.VERSI_NAMA} (${BuildConfig.VERSI_KODE})", fontSize = 11.sp, color = MutedFg)
        }

        Spacer(Modifier.weight(1f))

        // Tombol hapus akun
        OutlinedButton(
            onClick = { tanyaHapus = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Merah),
        ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Hapus Akun & Data")
        }

        // Tombol logout
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Merah),
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Keluar", fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(Modifier.height(16.dp))
    }

    if (tanyaHapus) {
        AlertDialog(
            onDismissRequest = { tanyaHapus = false },
            title = { Text("Hapus akun?") },
            text = { Text("Semua data kamu dan anak akan dihapus permanen. Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                TextButton(onClick = { tanyaHapus = false; onHapusAkun() }) {
                    Text("Hapus Permanen", color = Merah)
                }
            },
            dismissButton = { TextButton(onClick = { tanyaHapus = false }) { Text("Batal") } },
        )
    }
}
