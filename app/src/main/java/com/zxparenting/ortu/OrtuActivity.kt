package com.zxparenting.ortu

import android.net.Uri
import android.os.Bundle
import com.google.androidbrowserhelper.trusted.LauncherActivity

/** TWA launcher — semua UI ortu dari web. */
class OrtuActivity : LauncherActivity() {
    override fun getLaunchingUrl(): Uri =
        Uri.parse("https://zxparenting.zomet.my.id/ortu")
}
