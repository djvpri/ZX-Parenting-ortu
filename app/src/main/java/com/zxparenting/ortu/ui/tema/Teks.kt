package com.zxparenting.ortu.ui.tema

// ponytail: i18n scaffold ID/EN. Add keys saat layar dipakai.
// Upgrade: pindah ke strings.xml + per-language resource saat app rilis multi-negara.

enum class Bahasa { ID, EN }

object Teks {
    private var lang: Bahasa = Bahasa.ID

    fun setBahasa(b: Bahasa) { lang = b }
    fun bahasa(): Bahasa = lang

    private val dict = mapOf(
        "beranda" to mapOf(Bahasa.ID to "Beranda", Bahasa.EN to "Home"),
        "anak" to mapOf(Bahasa.ID to "Anak", Bahasa.EN to "Child"),
        "tugas" to mapOf(Bahasa.ID to "Tugas", Bahasa.EN to "Tasks"),
        "aturan" to mapOf(Bahasa.ID to "Aturan", Bahasa.EN to "Rules"),
        "profil" to mapOf(Bahasa.ID to "Profil", Bahasa.EN to "Profile"),
        "market" to mapOf(Bahasa.ID to "Market", Bahasa.EN to "Market"),
        "coin" to mapOf(Bahasa.ID to "ZX Coin", Bahasa.EN to "ZX Coin"),
        "quest" to mapOf(Bahasa.ID to "Quest", Bahasa.EN to "Quest"),
        "lokasi" to mapOf(Bahasa.ID to "Lokasi", Bahasa.EN to "Location"),
        "laporan" to mapOf(Bahasa.ID to "Laporan", Bahasa.EN to "Report"),
        "forum" to mapOf(Bahasa.ID to "Forum", Bahasa.EN to "Forum"),
        "logout" to mapOf(Bahasa.ID to "Keluar", Bahasa.EN to "Logout"),
        "hapus_akun" to mapOf(Bahasa.ID to "Hapus Akun", Bahasa.EN to "Delete Account"),
        "simpan" to mapOf(Bahasa.ID to "Simpan", Bahasa.EN to "Save"),
        "batal" to mapOf(Bahasa.ID to "Batal", Bahasa.EN to "Cancel"),
        "tambah" to mapOf(Bahasa.ID to "Tambah", Bahasa.EN to "Add"),
        "versi" to mapOf(Bahasa.ID to "Versi", Bahasa.EN to "Version"),
        "saldo" to mapOf(Bahasa.ID to "Saldo", Bahasa.EN to "Balance"),
        "topup" to mapOf(Bahasa.ID to "Top Up", Bahasa.EN to "Top Up"),
        "komentar" to mapOf(Bahasa.ID to "Komentar", Bahasa.EN to "Comments"),
        "ringkasan_anak" to mapOf(Bahasa.ID to "Anak", Bahasa.EN to "Children"),
        "perlu_validasi" to mapOf(Bahasa.ID to "Perlu Validasi", Bahasa.EN to "Needs Review"),
        "menu" to mapOf(Bahasa.ID to "Menu", Bahasa.EN to "Menu"),
        "keluarga" to mapOf(Bahasa.ID to "Keluarga", Bahasa.EN to "Family"),
        "hadiah" to mapOf(Bahasa.ID to "Hadiah", Bahasa.EN to "Rewards"),
        "perangkat" to mapOf(Bahasa.ID to "Perangkat", Bahasa.EN to "Devices"),
        "aktivitas" to mapOf(Bahasa.ID to "Aktivitas", Bahasa.EN to "Activity"),
        "gps" to mapOf(Bahasa.ID to "GPS", Bahasa.EN to "GPS"),
        "akun" to mapOf(Bahasa.ID to "Akun", Bahasa.EN to "Account"),
    )

    operator fun get(key: String): String =
        dict[key]?.get(lang) ?: key
}
