# ZX Parenting Ortu (TWA)

App Android **ortu** = Trusted Web Activity (TWA): membungkus web
`https://zxparenting.zomet.my.id/ortu` jadi APK/AAB resmi Play Store.
Tanpa UI native — semua UI dari web. Package `com.zxparenting.ortu`,
keystore sama dgn agent (`keystore/`).

## Rilis
Sama polanya dgn ZX-Parenting-agent:
- CI: `assembleDebug` tiap push.
- Rilis: Actions → `rilis` → Run workflow, input `versi` (mis. `1.0.0`).
  Workflow bump `alamat.json`, build `bundleRelease` (AAB) + `assembleRelease` (APK),
  attach keduanya ke GitHub Release.
- Keystore repo-first (folder `keystore/`), fallback Secrets.

## Digital Asset Links (sudah terpasang di web)
`public/.well-known/assetlinks.json` di repo `zx_parenting` memuat fingerprint
SHA-256 keystore `zxagent` + package `com.zxparenting.ortu`. Tanpa itu TWA
tampil dgn bar URL (bukan full-screen).
