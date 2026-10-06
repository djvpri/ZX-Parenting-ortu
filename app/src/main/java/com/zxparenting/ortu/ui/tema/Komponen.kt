package com.zxparenting.ortu.ui.tema

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KartuClay(
    modifier: Modifier = Modifier,
    kecil: Boolean = false,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = if (kecil) BentukKartuKecil else BentukKartu,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (kecil) 4.dp else 8.dp),
    ) {
        Box(modifier = Modifier.padding(if (kecil) 13.dp else 16.dp)) { content() }
    }
}

@Composable
fun TileMenu(
    judul: String,
    sub: String,
    warnaBg: Color,
    ikon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    KartuClay(modifier = modifier, kecil = true) {
        Column {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(warnaBg),
                contentAlignment = Alignment.Center,
            ) { ikon() }
            Spacer(Modifier.height(6.dp))
            Text(judul, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(sub, fontSize = 10.sp, color = MutedFg)
        }
    }
}

@Composable
fun BarMini(persen: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(10.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(Muted),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(persen.coerceIn(0f, 1f))
                .height(10.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(Brush.horizontalGradient(listOf(Biru, Ungu))),
        )
    }
}

@Composable
fun BadgePill(
    teks: String,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bg)
            .padding(horizontal = 11.dp, vertical = 5.dp),
    ) {
        Text(teks, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}

@Composable
fun BarisKV(k: String, v: String, vWarna: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(k, fontSize = 12.sp, color = MutedFg)
        Text(v, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = vWarna)
    }
}
