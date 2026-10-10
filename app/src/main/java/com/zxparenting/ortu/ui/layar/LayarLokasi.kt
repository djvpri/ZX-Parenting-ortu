package com.zxparenting.ortu.ui.layar

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.zxparenting.ortu.api.Device
import com.zxparenting.ortu.api.DevicePatch
import com.zxparenting.ortu.ui.tema.*
import androidx.compose.ui.res.stringResource
import com.zxparenting.ortu.R

@Composable
fun LayarLokasi(
    devices: List<Device>,
    onPatch: (deviceId: String, patch: DevicePatch) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        Text(stringResource(R.string.lokasi), style = MaterialTheme.typography.titleLarge)

        if (devices.isEmpty()) {
            KartuClay {
                Text("Belum ada perangkat terhubung.", fontSize = 12.sp, color = MutedFg)
            }
        }

        devices.forEach { dev ->
            KartuLokasiDevice(dev, onPatch)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun KartuLokasiDevice(
    dev: Device,
    onPatch: (String, DevicePatch) -> Unit,
) {
    var geoLat by remember(dev.id) { mutableStateOf(dev.geoLat?.toString() ?: "") }
    var geoLng by remember(dev.id) { mutableStateOf(dev.geoLng?.toString() ?: "") }
    var geoRadius by remember(dev.id) { mutableStateOf(dev.geoRadius?.toString() ?: "200") }
    var geoAktif by remember(dev.id) { mutableStateOf(dev.geoAktif ?: false) }

    KartuClay {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Merah, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(dev.nama, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(dev.anak?.nama ?: "-", fontSize = 11.sp, color = MutedFg)
                }
            }

            // GPS toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("GPS Pelacakan", fontSize = 12.sp)
                Switch(
                    checked = dev.gpsAktif ?: false,
                    onCheckedChange = { onPatch(dev.id, DevicePatch(gpsAktif = it)) },
                )
            }

            // Peta posisi anak
            val lat = dev.latNow
            val lng = dev.lngNow
            if (lat != null && lng != null) {
                Text("Posisi Terakhir", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
                PetaOsm(lat, lng, modifier = Modifier.fillMaxWidth().height(200.dp))
                Text("Koordinat: %.5f, %.5f".format(lat, lng), fontSize = 10.sp, color = MutedFg)
            } else {
                KartuClay(kecil = true) {
                    Text("GPS belum melaporkan posisi.", fontSize = 11.sp, color = MutedFg)
                }
            }

            HorizontalDivider(color = Border)

            // Geofence setup
            Text("Zona Aman (Geofence)", fontSize = 11.sp, color = MutedFg, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldTeks("Latitude", geoLat, Modifier.weight(1f), KeyboardType.Decimal) { geoLat = it }
                FieldTeks("Longitude", geoLng, Modifier.weight(1f), KeyboardType.Decimal) { geoLng = it }
            }
            FieldTeks("Radius (meter, 50-2000)", geoRadius, Modifier.fillMaxWidth(), KeyboardType.Number) { geoRadius = it }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Aktifkan zona", fontSize = 12.sp)
                Switch(checked = geoAktif, onCheckedChange = {
                    geoAktif = it
                    val lat = geoLat.toDoubleOrNull()
                    val lng = geoLng.toDoubleOrNull()
                    val rad = geoRadius.toIntOrNull()
                    if (it && lat != null && lng != null && rad != null) {
                        onPatch(dev.id, DevicePatch(geoAktif = true, geoLat = lat, geoLng = lng, geoRadius = rad))
                    } else if (!it) {
                        onPatch(dev.id, DevicePatch(geoAktif = false))
                    }
                })
            }

            Button(
                onClick = {
                    val lat = geoLat.toDoubleOrNull()
                    val lng = geoLng.toDoubleOrNull()
                    val rad = geoRadius.toIntOrNull()
                    if (lat != null && lng != null && rad != null) {
                        onPatch(dev.id, DevicePatch(geoAktif = geoAktif, geoLat = lat, geoLng = lng, geoRadius = rad))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(14.dp),
            ) { Text("Simpan Zona") }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PetaOsm(lat: Double, lng: Double, modifier: Modifier = Modifier) {
    // OSM embed: bbox = center ± 0.005 derajat (~500m)
    val d = 0.005
    val bbox = "${lng - d},${lat - d},${lng + d},${lat + d}"
    val url = "https://www.openstreetmap.org/export/embed.html?bbox=$bbox&layer=mapnik&marker=$lat,$lng"

    AndroidView(
        modifier = modifier.clip(RoundedCornerShape(14.dp)),
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        },
        update = { it.loadUrl(url) },
    )
}

@Composable
private fun FieldTeks(
    label: String,
    value: String,
    modifier: Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    onubah: (String) -> Unit,
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 10.sp, color = MutedFg)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onubah,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
        )
    }
}
