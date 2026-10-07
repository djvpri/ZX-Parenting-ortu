package com.zxparenting.ortu.ui.tema

// ponytail: i18n scaffold ID/EN. Add keys saat layar dipakai.
// Upgrade: pindah ke strings.xml + per-language resource saat app rilis multi-negara.

enum class Bahasa { ID, EN }

object Teks {
    private var lang: Bahasa = Bahasa.ID

    fun setBahasa(b: Bahasa) { lang = b }
    fun bahasa(): Bahasa = lang

    private val dict = mapOf(
        // Bottom tab
        "beranda" to mapOf(Bahasa.ID to "Beranda", Bahasa.EN to "Home"),
        "anak" to mapOf(Bahasa.ID to "Anak", Bahasa.EN to "Child"),
        "tugas" to mapOf(Bahasa.ID to "Tugas", Bahasa.EN to "Tasks"),
        "aturan" to mapOf(Bahasa.ID to "Aturan", Bahasa.EN to "Rules"),
        "profil" to mapOf(Bahasa.ID to "Profil", Bahasa.EN to "Profile"),
        "market" to mapOf(Bahasa.ID to "Market", Bahasa.EN to "Market"),
        // Tile menu
        "coin" to mapOf(Bahasa.ID to "ZX Coin", Bahasa.EN to "ZX Coin"),
        "quest" to mapOf(Bahasa.ID to "Quest", Bahasa.EN to "Quest"),
        "lokasi" to mapOf(Bahasa.ID to "Lokasi", Bahasa.EN to "Location"),
        "laporan" to mapOf(Bahasa.ID to "Laporan", Bahasa.EN to "Report"),
        "forum" to mapOf(Bahasa.ID to "Forum", Bahasa.EN to "Forum"),
        "langganan" to mapOf(Bahasa.ID to "Langganan", Bahasa.EN to "Subscription"),
        "pesan" to mapOf(Bahasa.ID to "Pesan", Bahasa.EN to "Messages"),
        "insights" to mapOf(Bahasa.ID to "Insights", Bahasa.EN to "Insights"),
        // Tile sublabel
        "profil_jumlah" to mapOf(Bahasa.ID to "profil", Bahasa.EN to "profiles"),
        "tugas_jumlah" to mapOf(Bahasa.ID to "tugas", Bahasa.EN to "tasks"),
        "hadiah" to mapOf(Bahasa.ID to "Hadiah", Bahasa.EN to "Rewards"),
        "perangkat_jumlah" to mapOf(Bahasa.ID to "perangkat", Bahasa.EN to "devices"),
        "diskusi" to mapOf(Bahasa.ID to "Diskusi", Bahasa.EN to "Discussion"),
        "keluarga" to mapOf(Bahasa.ID to "Keluarga", Bahasa.EN to "Family"),
        "aktivitas" to mapOf(Bahasa.ID to "Aktivitas", Bahasa.EN to "Activity"),
        "chat" to mapOf(Bahasa.ID to "Chat", Bahasa.EN to "Chat"),
        "ai" to mapOf(Bahasa.ID to "AI", Bahasa.EN to "AI"),
        "gps" to mapOf(Bahasa.ID to "GPS", Bahasa.EN to "GPS"),
        // Aksi
        "logout" to mapOf(Bahasa.ID to "Keluar", Bahasa.EN to "Logout"),
        "hapus_akun" to mapOf(Bahasa.ID to "Hapus Akun", Bahasa.EN to "Delete Account"),
        "simpan" to mapOf(Bahasa.ID to "Simpan", Bahasa.EN to "Save"),
        "batal" to mapOf(Bahasa.ID to "Batal", Bahasa.EN to "Cancel"),
        "tambah" to mapOf(Bahasa.ID to "Tambah", Bahasa.EN to "Add"),
        "kirim" to mapOf(Bahasa.ID to "Kirim", Bahasa.EN to "Send"),
        "kembali" to mapOf(Bahasa.ID to "← Kembali", Bahasa.EN to "← Back"),
        "buka" to mapOf(Bahasa.ID to "Buka", Bahasa.EN to "Open"),
        // Umum
        "versi" to mapOf(Bahasa.ID to "Versi", Bahasa.EN to "Version"),
        "saldo" to mapOf(Bahasa.ID to "Saldo", Bahasa.EN to "Balance"),
        "topup" to mapOf(Bahasa.ID to "Top Up", Bahasa.EN to "Top Up"),
        "komentar" to mapOf(Bahasa.ID to "Komentar", Bahasa.EN to "Comments"),
        "menu" to mapOf(Bahasa.ID to "Menu", Bahasa.EN to "Menu"),
        "perangkat" to mapOf(Bahasa.ID to "Perangkat", Bahasa.EN to "Devices"),
        "akun" to mapOf(Bahasa.ID to "Akun", Bahasa.EN to "Account"),
        "ringkasan_anak" to mapOf(Bahasa.ID to "Anak", Bahasa.EN to "Children"),
        "perlu_validasi" to mapOf(Bahasa.ID to "Perlu Validasi", Bahasa.EN to "Needs Review"),
        // Insights
        "peringatan" to mapOf(Bahasa.ID to "Peringatan", Bahasa.EN to "Warning"),
        "positif" to mapOf(Bahasa.ID to "Positif", Bahasa.EN to "Positive"),
        "info" to mapOf(Bahasa.ID to "Info", Bahasa.EN to "Info"),
        "ringkasan" to mapOf(Bahasa.ID to "Ringkasan", Bahasa.EN to "Summary"),
        "pilih_anak" to mapOf(Bahasa.ID to "Pilih Anak", Bahasa.EN to "Select Child"),
        "total_aktivitas" to mapOf(Bahasa.ID to "Total Aktivitas", Bahasa.EN to "Total Activity"),
        "tugas_selesai" to mapOf(Bahasa.ID to "Tugas Selesai", Bahasa.EN to "Tasks Done"),
        "tugas_pending" to mapOf(Bahasa.ID to "Tugas Pending", Bahasa.EN to "Tasks Pending"),
        "saldo_token" to mapOf(Bahasa.ID to "Saldo Token", Bahasa.EN to "Token Balance"),
        "menit" to mapOf(Bahasa.ID to "menit", Bahasa.EN to "minutes"),
        "hari" to mapOf(Bahasa.ID to "hari", Bahasa.EN to "days"),
        "belum_pesan" to mapOf(Bahasa.ID to "Belum ada percakapan.", Bahasa.EN to "No conversations yet."),
        "tulis_pesan" to mapOf(Bahasa.ID to "Tulis pesan...", Bahasa.EN to "Type a message..."),
        // Langganan
        "tier_trial" to mapOf(Bahasa.ID to "Trial", Bahasa.EN to "Trial"),
        "tier_pro" to mapOf(Bahasa.ID to "Pro", Bahasa.EN to "Pro"),
        "tier_elite" to mapOf(Bahasa.ID to "Elite", Bahasa.EN to "Elite"),
        "trial_aktif" to mapOf(Bahasa.ID to "Trial Aktif", Bahasa.EN to "Trial Active"),
        "trial_habis" to mapOf(Bahasa.ID to "Trial Habis", Bahasa.EN to "Trial Expired"),
        "upgrade" to mapOf(Bahasa.ID to "Upgrade", Bahasa.EN to "Upgrade"),
        "paket" to mapOf(Bahasa.ID to "Paket", Bahasa.EN to "Plan"),
        "limit_anak" to mapOf(Bahasa.ID to "Limit Anak", Bahasa.EN to "Child Limit"),
        // Pinjam Waktu
        "sumber_ai" to mapOf(Bahasa.ID to "Sumber", Bahasa.EN to "Source"),
        "pinjam_waktu" to mapOf(Bahasa.ID to "Pinjam Waktu", Bahasa.EN to "Borrow Time"),
        "hutang" to mapOf(Bahasa.ID to "Hutang Token", Bahasa.EN to "Token Debt"),
        "riwayat_pinjam" to mapOf(Bahasa.ID to "Riwayat", Bahasa.EN to "History"),
        "belum_riwayat" to mapOf(Bahasa.ID to "Belum ada riwayat pinjaman.", Bahasa.EN to "No loan history yet."),
    )

    operator fun get(key: String): String =
        dict[key]?.get(lang) ?: key
}
