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
import com.zxparenting.ortu.R

@Composable
fun LayarLogin(
    state: UiState,
    onLogin: (String, String) -> Unit,
    onKeDaftar: () -> Unit,
    onGoogle: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var lihatPass by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Logo / header
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
        Text("ZX Parenting", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Text("Masuk sebagai orang tua", fontSize = 13.sp, color = MutedFg, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        )
        Spacer(Modifier.height(12.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
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
        Spacer(Modifier.height(20.dp))

        if (state.error != null) {
            Text(
                state.error,
                color = Merah,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }

        Button(
            onClick = { onLogin(email, password) },
            enabled = !state.loading && email.isNotBlank() && password.isNotBlank(),
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
                Text(stringResource(R.string.masuk), fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
            }
        }

        // Pemisah stringResource(R.string.atau)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(stringResource(R.string.atau), fontSize = 12.sp, color = MutedFg, modifier = Modifier.padding(horizontal = 12.dp))
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        // Tombol Google — Credential Manager dipanggil dari Activity (onGoogle)
        OutlinedButton(
            onClick = onGoogle,
            enabled = !state.loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text("Masuk dengan Google", fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }

        Spacer(Modifier.height(20.dp))

        TextButton(onClick = onKeDaftar) {
            Text("Belum punya akun? Daftar", fontSize = 13.sp, color = Biru)
        }

        Spacer(Modifier.height(12.dp))
        Text(
            "ZX Parenting v${com.zxparenting.ortu.BuildConfig.VERSI_NAMA} (${com.zxparenting.ortu.BuildConfig.VERSI_KODE})",
            fontSize = 10.sp,
            color = MutedFg,
            textAlign = TextAlign.Center,
        )
    }
}
