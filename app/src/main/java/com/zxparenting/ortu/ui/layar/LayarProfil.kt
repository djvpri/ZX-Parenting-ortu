package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.BuildConfig
import com.zxparenting.ortu.PemeriksaPembaruan
import com.zxparenting.ortu.ui.tema.*
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.filled.Translate
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Column
import com.zxparenting.ortu.ManajerBahasa
import com.zxparenting.ortu.R
import android.app.Activity

@Composable
fun LayarProfil(
    nama: String?,
    onLogout: () -> Unit,
    onHapusAkun: () -> Unit,
) {
    var tanyaHapus by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    val daftarTema = remember { TemaRepo.daftarTema() }
    var temaAktif by remember { mutableStateOf(PengaturanTema.muat(ctx)) }

    fun gantiTema(key: String) {
        temaAktif = key
        PengaturanTema.simpan(ctx, key)
        WarnaAktif.gantiByKey(key)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text(stringResource(R.string.profil), style = MaterialTheme.typography.titleLarge)

        KartuClay(kecil = true) {
            Column {
                Text(stringResource(R.string.nama), fontSize = 10.sp, color = MutedFg, fontWeight = FontWeight.Bold)
                Text(nama ?: "-", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        KartuClay(kecil = true) {
            Text("ZX Parenting v${BuildConfig.VERSI_NAMA} (${BuildConfig.VERSI_KODE})", fontSize = 11.sp, color = MutedFg)
        }

        // Pembaruan otomatis
        var statusUpdate by remember { mutableStateOf<String?>(null) }
        var sedangCek by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            sedangCek = true
            PemeriksaPembaruan.periksa(ctx) { tag ->
                sedangCek = false
                statusUpdate = if (tag != null) "Memasang v${tag.removePrefix("v")}…" else null
            }
        }
        KartuClay(kecil = true) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    when {
                        sedangCek -> "Memeriksa pembaruan…"
                        statusUpdate != null -> statusUpdate!!
                        else -> "App terbaru"
                    },
                    fontSize = 11.sp,
                    color = MutedFg,
                )
                TextButton(onClick = {
                    sedangCek = true
                    PemeriksaPembaruan.periksa(ctx) { tag ->
                        sedangCek = false
                        statusUpdate = if (tag != null) "Memasang v${tag.removePrefix("v")}…" else null
                    }
                }) {
                    Text(stringResource(R.string.periksa), fontSize = 11.sp)
                }
            }
        }

        // Toggle bahasa
        KartuClay(kecil = true) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.bahasa), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row {
                    FilterChip(
                        selected = Teks.bahasa() == Bahasa.ID,
                        onClick = { Teks.setBahasa(Bahasa.ID) },
                        label = { Text(stringResource(R.string.id), fontSize = 11.sp) },
                    )
                    Spacer(Modifier.width(6.dp))
                    FilterChip(
                        selected = Teks.bahasa() == Bahasa.EN,
                        onClick = { Teks.setBahasa(Bahasa.EN) },
                        label = { Text(stringResource(R.string.en), fontSize = 11.sp) },
                    )
                }
            }
        }

        // Tema picker
        KartuClay(kecil = true) {
            Column {
                Text(stringResource(R.string.tema), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                daftarTema.forEach { key ->
                    val palet = remember(key) { TemaRepo.load(key) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Preview swatch
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Box(modifier = Modifier.size(20.dp).clip(RoundedCornerShape(6.dp)).background(palet.bg))
                            Box(modifier = Modifier.size(20.dp).clip(RoundedCornerShape(6.dp)).background(palet.utama))
                            Box(modifier = Modifier.size(20.dp).clip(RoundedCornerShape(6.dp)).background(palet.aksen))
                            Box(modifier = Modifier.size(20.dp).clip(RoundedCornerShape(6.dp)).background(palet.hijau))
                            Box(modifier = Modifier.size(20.dp).clip(RoundedCornerShape(6.dp)).background(palet.merah))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(palet.nama, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        RadioButton(
                            selected = temaAktif == key,
                            onClick = { gantiTema(key) },
                        )
                    }
                }
            }
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

        // Language switcher
        val ctx = LocalContext.current
        val bahasaAktif = ManajerBahasa.bahasaAktif(ctx)
        var showLangDialog by remember { mutableStateOf(false) }
        OutlinedButton(
            onClick = { showLangDialog = true },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.language))
        }
        Spacer(Modifier.height(8.dp))

        if (showLangDialog) {
            AlertDialog(
                onDismissRequest = { showLangDialog = false },
                title = { Text(stringResource(R.string.language)) },
                text = {
                    Column {
                        listOf("en" to "English", "in" to "Indonesian").forEach { (kode, nama) ->
                            TextButton(
                                onClick = {
                                    ManajerBahasa.setBahasa(ctx, kode)
                                    showLangDialog = false
                                    (ctx as? Activity)?.recreate()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (bahasaAktif == kode) "✓ $nama" else nama)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLangDialog = false }) {
                        Text(stringResource(R.string.batal))
                    }
                }
            )
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
            Text(stringResource(R.string.keluar), fontWeight = FontWeight.Bold, color = Color.White)
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
            dismissButton = { TextButton(onClick = { tanyaHapus = false }) { Text(stringResource(R.string.batal)) } },
        )
    }
}
