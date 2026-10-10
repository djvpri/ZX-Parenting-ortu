package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.api.ForumPost
import com.zxparenting.ortu.ui.tema.*
import androidx.compose.ui.res.stringResource

@Composable
fun LayarForum(
    posts: List<ForumPost>,
    detail: ForumPost?,
    loading: Boolean,
    onBuatPost: (judul: String, isi: String, kategori: String) -> Unit,
    onBukaPost: (String) -> Unit,
    onHapusPost: (String) -> Unit,
    onKomentar: (String, String) -> Unit,
    onBack: () -> Unit,
) {
    if (detail != null) {
        DetailPost(detail, loading, onKomentar, onBack)
        return
    }

    var tampilForm by remember { mutableStateOf(false) }

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
            Text("Forum Keluarga", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { tampilForm = !tampilForm }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.post))
            }
        }

        if (tampilForm) {
            FormPost(loading = loading) { j, i, k ->
                onBuatPost(j, i, k)
                tampilForm = false
            }
        }

        if (posts.isEmpty()) {
            KartuClay {
                Text("Belum ada post. Bagikan tips atau pertanyaan!", fontSize = 12.sp, color = MutedFg)
            }
        }

        posts.forEach { p ->
            KartuPost(p, onBuka = { onBukaPost(p.id) }, onHapus = { onHapusPost(p.id) })
        }
    }
}

@Composable
private fun KartuPost(p: ForumPost, onBuka: () -> Unit, onHapus: () -> Unit) {
    KartuClay(kecil = true) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Ungu.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Forum, contentDescription = null, tint = Ungu, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(p.judul, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("${p.ortu.nama} • ${p.createdAt.take(10)}", fontSize = 10.sp, color = MutedFg)
                }
                BadgePill(
                    teks = p.kategori,
                    bg = Color(0xFFE0E7FF),
                    fg = Color(0xFF3730A3),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(p.isi.take(120) + if (p.isi.length > 120) "…" else "", fontSize = 11.sp, color = MutedFg)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Comment, contentDescription = null, tint = MutedFg, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${p._count?.komentar ?: 0}", fontSize = 10.sp, color = MutedFg)
                }
                TextButton(onClick = onBuka) { Text(stringResource(R.string.buka), fontSize = 11.sp) }
            }
        }
    }
}

@Composable
private fun FormPost(
    loading: Boolean,
    onBuat: (judul: String, isi: String, kategori: String) -> Unit,
) {
    var judul by remember { mutableStateOf("") }
    var isi by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("umum") }

    val kategoriOpt = listOf("umum" to "Umum", "tips" to "Tips", "pertanyaan" to "Pertanyaan")

    KartuClay {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Buat Post", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = judul,
                onValueChange = { judul = it },
                label = { Text(stringResource(R.string.judul)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
            )
            OutlinedTextField(
                value = isi,
                onValueChange = { isi = it },
                label = { Text(stringResource(R.string.isi)) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                kategoriOpt.forEach { (k, label) ->
                    FilterChip(
                        selected = kategori == k,
                        onClick = { kategori = k },
                        label = { Text(label, fontSize = 11.sp) },
                    )
                }
            }
            Button(
                onClick = {
                    if (judul.isNotBlank() && isi.isNotBlank()) {
                        onBuat(judul.trim(), isi.trim(), kategori)
                    }
                },
                enabled = !loading && judul.isNotBlank() && isi.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.post))
                }
            }
        }
    }
}

@Composable
private fun DetailPost(
    post: ForumPost,
    loading: Boolean,
    onKomentar: (String, String) -> Unit,
    onBack: () -> Unit,
) {
    var komentarBaru by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
            Text(stringResource(R.string.forum), style = MaterialTheme.typography.titleLarge)
        }

        // Post
        KartuClay {
            Column {
                Text(post.judul, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("${post.ortu.nama} • ${post.createdAt.take(10)}", fontSize = 10.sp, color = MutedFg)
                Spacer(Modifier.height(8.dp))
                Text(post.isi, fontSize = 13.sp)
            }
        }

        // Komentar
        Text("Komentar (${post.komentar?.size ?: 0})", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
        post.komentar?.forEach { k ->
            KartuClay(kecil = true) {
                Column {
                    Text(k.ortu.nama, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(k.isi, fontSize = 12.sp)
                    Text(k.createdAt.take(10), fontSize = 9.sp, color = MutedFg)
                }
            }
        }

        // Form komentar
        KartuClay {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = komentarBaru,
                    onValueChange = { komentarBaru = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tulis komentar…", fontSize = 12.sp) },
                    shape = RoundedCornerShape(12.dp),
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (komentarBaru.isNotBlank()) {
                            onKomentar(post.id, komentarBaru.trim())
                            komentarBaru = ""
                        }
                    },
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Kirim", tint = Ungu)
                }
            }
        }
    }
}
