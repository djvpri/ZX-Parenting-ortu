package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zxparenting.ortu.ui.tema.*

@Composable
fun LayarNotif() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text("Notifikasi", style = MaterialTheme.typography.titleLarge)
        KartuClay(kecil = true) {
            Text("Belum ada notifikasi.", fontSize = 12.sp, color = MutedFg)
        }
    }
}
