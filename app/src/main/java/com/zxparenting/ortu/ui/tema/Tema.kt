package com.zxparenting.ortu.ui.tema

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Singleton palet aktif. Swap runtime: WarnaAktif.ganti(palet).
// Compose auto-recompose semua pemanggilan val Bg/Biru/dst.
object WarnaAktif {
    var palet by mutableStateOf(Palet.DEFAULT)
        private set

    fun ganti(p: Palet) { palet = p }
    fun gantiByKey(key: String) { palet = TemaRepo.load(key) }
}

// Computed property — baca dari palet aktif. Nol perubahan di call site.
val Bg get() = WarnaAktif.palet.bg
val Kartu get() = WarnaAktif.palet.kartu
val Biru get() = WarnaAktif.palet.biru
val BiruGelap get() = WarnaAktif.palet.biruGelap
val Ungu get() = WarnaAktif.palet.ungu
val Amber get() = WarnaAktif.palet.amber
val Hijau get() = WarnaAktif.palet.hijau
val Merah get() = WarnaAktif.palet.merah
val Pink get() = WarnaAktif.palet.pink
val Muted get() = WarnaAktif.palet.muted
val MutedFg get() = WarnaAktif.palet.teksMuted
val Border get() = WarnaAktif.palet.border

private val SkemaTerang = lightColorScheme(
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

private val SkemaGelap = darkColorScheme(
    primary = Biru,
    secondary = Amber,
    tertiary = Ungu,
    background = Bg,
    surface = Kartu,
    onPrimary = Color.White,
    onSecondary = Color(0xFF78350F),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
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
    val p = WarnaAktif.palet
    val gelap = hitungLuminansi(p.bg) < 0.5f
    val skema = if (gelap) SkemaGelap else SkemaTerang
    MaterialTheme(
        colorScheme = skema,
        typography = Tipografi,
        shapes = Shapes(
            small = RoundedCornerShape(RadiusKecil),
            medium = BentukKartuKecil,
            large = BentukKartu,
        ),
        content = content,
    )
}

// ponytail: luminansi sederhana. Upgrade: copyFrom() Color.luminance() saat tersedia.
private fun hitungLuminansi(c: Color): Float {
    val r = c.red
    val g = c.green
    val b = c.blue
    return 0.299f * r + 0.587f * g + 0.114f * b
}
