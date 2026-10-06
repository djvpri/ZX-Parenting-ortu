package com.zxparenting.ortu.ui.tema

import androidx.compose.foundation.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

// Token dari mockup ortu-ui-mockup.html (claymorphism dewasa)
val Biru = Color(0xFF2563EB)
val BiruGelap = Color(0xFF1E3A8A)
val Ungu = Color(0xFF7C3AED)
val Amber = Color(0xFFF59E0B)
val Hijau = Color(0xFF16A34A)
val Merah = Color(0xFFDC2626)
val Pink = Color(0xFFEC4899)
val Bg = Color(0xFFEFF6FF)
 val Kartu = Color(0xFFFFFFFF)
val Muted = Color(0xFFF1F5FD)
val MutedFg = Color(0xFF475569)
val Border = Color(0xFFE4ECFC)

val SkemaWarna = lightColorScheme(
    primary = Biru,
    secondary = Amber,
    tertiary = Ungu,
    background = Bg,
    surface = Kartu,
    onPrimary = Color.White,
    onSecondary = Color(0xFF92400E),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    error = Merah,
)

val Tipografi = Typography(
    titleLarge = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 19.sp),
    titleMedium = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp),
    bodyMedium = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
    bodySmall = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = MutedFg),
    labelSmall = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold),
)

val RadiusBesar = 22.dp
val RadiusKartu = 18.dp
val RadiusKecil = 12.dp

val BentukKartu = RoundedCornerShape(RadiusBesar)
val BentukKartuKecil = RoundedCornerShape(RadiusKartu)

@Composable
fun TemaZX(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SkemaWarna,
        typography = Tipografi,
        shapes = Shapes(
            small = RoundedCornerShape(RadiusKecil),
            medium = BentukKartuKecil,
            large = BentukKartu,
        ),
        content = content,
    )
}
