package com.zxparenting.ortu

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.androidbrowserhelper.trusted.LauncherActivity

/** TWA launcher — semua UI ortu dari web (pola TWA resmi). */
class OrtuActivity : LauncherActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Link https://zxparenting.zomet.my.id/ortu/* ditangani TWA langsung.
        setCustomTabsBuilder(null)
    }

    override fun getLaunchingUrl(): Uri =
        Uri.parse("https://zxparenting.zomet.my.id/ortu")
}
