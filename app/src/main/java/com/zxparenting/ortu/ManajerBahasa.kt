package com.zxparenting.ortu

import android.content.Context
import android.os.Build
import java.util.Locale

/**
 * Manajemen bahasa app. Default = English (values/strings.xml).
 * Opsi: Indonesian (values-in/strings.xml).
 * ponytail: MVP ContextWrapper locale. Upgrade AppCompatDelegate atau per-app-locale API (Android 13+) jika perlu.
 */
object ManajerBahasa {
    private const val PREF = "bahasa_pref"
    private const val KEY = "bahasa"

    fun inisialisasi(ctx: Context) {
        // No-op: locale handled via ContextWrapper at attachBaseContext
        val prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val kode = prefs.getString(KEY, "en") ?: "en"
        // Set default locale for process
        val locale = when (kode) {
            "in" -> Locale("in")
            else -> Locale("en")
        }
        Locale.setDefault(locale)
    }

    fun setBahasa(ctx: Context, kode: String) {
        val prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY, kode).apply()
        // Recreate activity to apply
        // Call site should recreate after this
    }

    fun bahasaAktif(ctx: Context): String {
        val prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return prefs.getString(KEY, "en") ?: "en"
    }

    fun localeAktif(ctx: Context): Locale {
        val prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val kode = prefs.getString(KEY, "en") ?: "en"
        return when (kode) {
            "in" -> Locale("in")
            else -> Locale("en")
        }
    }
}
