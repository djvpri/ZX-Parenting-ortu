package com.zxparenting.ortu.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("zxortu")

object Kunci {
    val TOKEN = stringPreferencesKey("token")
    val NAMA = stringPreferencesKey("nama")
    val USER_ID = stringPreferencesKey("uid")
}

class Simpanan(val ctx: Context) {
    val token: Flow<String?> = ctx.dataStore.data.map { it[Kunci.TOKEN] }
    val nama: Flow<String?> = ctx.dataStore.data.map { it[Kunci.NAMA] }

    suspend fun simpanSesi(token: String, nama: String, uid: String) {
        ctx.dataStore.edit {
            it[Kunci.TOKEN] = token
            it[Kunci.NAMA] = nama
            it[Kunci.USER_ID] = uid
        }
    }

    suspend fun hapus() {
        ctx.dataStore.edit { it.clear() }
    }
}
