package com.zxparenting.ortu

import android.content.Context
import android.util.Log
import com.zxparenting.ortu.data.Simpanan
import com.zxparenting.ortu.api.Klien
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Fire-and-forget error reporter untuk app ortu.
 * Kirim error ke /api/ortu/lapor-error via Bearer JWT.
 * Gagal kirim = diam-diam (log ke logcat saja).
 */
object LaporError {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private const val TAG = "LaporError"

    fun kirim(context: Context, screen: String, message: String, extra: Map<String, Any?>? = null) {
        scope.launch {
            runCatching {
                val token = Simpanan(context).token.first() ?: return@runCatching
                val body = JSONObject()
                    .put("screen", screen)
                    .put("message", message)
                if (extra != null) {
                    val ex = JSONObject()
                    extra.forEach { (k, v) -> ex.put(k, v) }
                    body.put("extra", ex)
                }

                val conn = URL("https://zxparenting.zomet.my.id/api/ortu/lapor-error").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.connectTimeout = 10_000
                conn.readTimeout = 10_000
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Authorization", "Bearer $token")
                conn.outputStream.use { os ->
                    OutputStreamWriter(os).write(body.toString())
                }
                conn.responseCode
                conn.disconnect()
            }.onFailure { Log.w(TAG, "gagal kirim error", it) }
        }
    }
}
