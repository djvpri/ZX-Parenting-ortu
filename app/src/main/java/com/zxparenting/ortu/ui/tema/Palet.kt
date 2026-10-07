package com.zxparenting.ortu.ui.tema

import android.content.Context
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import org.json.JSONObject
import java.io.IOException

// ponytail: JSON palette. Tambah tema = drop file JSON di assets/tema/.
// Upgrade: download tema remote saat server theme marketplace live.

data class Palet(
    val nama: String,
    val bg: Color,
    val kartu: Color,
    val utama: Color,
    val aksen: Color,
    val teks: Color,
    val teksMuted: Color,
    val muted: Color,
    val border: Color,
    val biru: Color,
    val ungu: Color,
    val amber: Color,
    val hijau: Color,
    val merah: Color,
    val pink: Color,
    val biruGelap: Color,
) {
    companion object {
        val DEFAULT = Palet(
            nama = "Clay (Default)",
            bg = Color(0xFFEFF6FF),
            kartu = Color(0xFFFFFFFF),
            utama = Color(0xFF2563EB),
            aksen = Color(0xFFF59E0B),
            teks = Color(0xFF0F172A),
            teksMuted = Color(0xFF475569),
            muted = Color(0xFFF1F5FD),
            border = Color(0xFFE4ECFC),
            biru = Color(0xFF2563EB),
            ungu = Color(0xFF7C3AED),
            amber = Color(0xFFF59E0B),
            hijau = Color(0xFF16A34A),
            merah = Color(0xFFDC2626),
            pink = Color(0xFFEC4899),
            biruGelap = Color(0xFF1E3A8A),
        )

        fun dariJson(nama: String, json: JSONObject): Palet {
            fun h(key: String, fallback: Long): Color {
                val s = json.optString(key, "")
                return if (s.isBlank()) Color(fallback)
                else try { Color(AndroidColor.parseColor(s)) }
                catch (_: Exception) { Color(fallback) }
            }
            return Palet(
                nama = json.optString("nama", nama),
                bg = h("bg", 0xFFEFF6FF),
                kartu = h("kartu", 0xFFFFFFFF),
                utama = h("utama", 0xFF2563EB),
                aksen = h("aksen", 0xFFF59E0B),
                teks = h("teks", 0xFF0F172A),
                teksMuted = h("teksMuted", 0xFF475569),
                muted = h("muted", 0xFFF1F5FD),
                border = h("border", 0xFFE4ECFC),
                biru = h("biru", 0xFF2563EB),
                ungu = h("ungu", 0xFF7C3AED),
                amber = h("amber", 0xFFF59E0B),
                hijau = h("hijau", 0xFF16A34A),
                merah = h("merah", 0xFFDC2626),
                pink = h("pink", 0xFFEC4899),
                biruGelap = h("biruGelap", 0xFF1E3A8A),
            )
        }
    }
}

object TemaRepo {
    private const val DIR = "tema"
    const val DEFAULT_KEY = "default"

    private var ctx: Context? = null

    fun init(context: Context) { ctx = context.applicationContext }

    fun daftarTema(): List<String> {
        val c = ctx ?: return listOf(DEFAULT_KEY)
        return try {
            c.assets.list(DIR)?.toList()?.map { it.removeSuffix(".json") }?.sorted()
                ?: listOf(DEFAULT_KEY)
        } catch (_: IOException) { listOf(DEFAULT_KEY) }
    }

    fun load(key: String): Palet {
        val c = ctx ?: return Palet.DEFAULT
        return try {
            val raw = c.assets.open("$DIR/$key.json").bufferedReader().use { it.readText() }
            Palet.dariJson(key, JSONObject(raw))
        } catch (_: IOException) {
            Palet.DEFAULT
        }
    }
}
