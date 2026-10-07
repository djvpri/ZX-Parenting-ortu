package com.zxparenting.ortu.ui.tema

import android.content.Context
import android.content.SharedPreferences

// ponytail: SharedPreferences untuk persist tema. Upgrade: DataStore saat settings kompleks.
object PengaturanTema {
    private const val FILE = "zx_pengaturan"
    private const val KEY_TEMA = "tema_key"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun simpan(ctx: Context, key: String) {
        prefs(ctx).edit().putString(KEY_TEMA, key).apply()
    }

    fun muat(ctx: Context): String =
        prefs(ctx).getString(KEY_TEMA, TemaRepo.DEFAULT_KEY) ?: TemaRepo.DEFAULT_KEY

    fun terapkan(ctx: Context) {
        val key = muat(ctx)
        WarnaAktif.gantiByKey(key)
    }
}
