package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarProfil(nama: String?, onLogout: () -> Unit) {
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
            Text("ZX Parenting v2.0.0", fontSize = 11.sp, color = MutedFg)
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Merah),
        ) {
            Text("Keluar", fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(Modifier.height(16.dp))
    }
}
