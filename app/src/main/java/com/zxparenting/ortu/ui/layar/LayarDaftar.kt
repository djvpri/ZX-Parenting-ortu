package com.zxparenting.ortu.ui.layar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxparenting.ortu.ui.UiState
import com.zxparenting.ortu.ui.tema.*
import androidx.compose.ui.res.stringResource

@Composable
fun LayarDaftar(state: UiState, onDaftar: (String, String, String) -> Unit, onKeLogin: () -> Unit) {
    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var lihatPass by remember { mutableStateOf(false) }
    var setuju by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(Biru, Ungu))),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(R.string.zx), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text("Daftar Akun Ortu", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Text("Gratis 30 hari (Free Trial)", fontSize = 13.sp, color = MutedFg, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text(stringResource(R.string.nama)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (min. 6)") },
            singleLine = true,
            visualTransformation = if (lihatPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { lihatPass = !lihatPass }) {
                    Icon(
                        imageVector = if (lihatPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Lihat password",
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        )
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = setuju, onCheckedChange = { setuju = it })
            Text(
                "Saya setuju pengelolaan data anak (consent ortu)",
                fontSize = 12.sp,
                color = MutedFg,
            )
        }
        Spacer(Modifier.height(16.dp))

        if (state.error != null) {
            Text(
                state.error,
                color = Merah,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }

        Button(
            onClick = { onDaftar(nama, email, password) },
            enabled = !state.loading && setuju && nama.isNotBlank() && email.isNotBlank() && password.length >= 6,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            if (state.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(stringResource(R.string.daftar), fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
            }
        }
        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onKeLogin) {
            Text("Sudah punya akun? Masuk", fontSize = 13.sp, color = Biru)
        }
    }
}
