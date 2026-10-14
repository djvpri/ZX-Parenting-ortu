# ZX-Parenting-Ortu — Development Guide

Panduan untuk AI agent dan developer yang bekerja di codebase app ortu ini.

## Apa Ini

APK Android (Kotlin + Jetpack Compose + Material 3) untuk **orang tua**
dalam ekosistem ZX Parenting. Ortu kelola anak, tugas, aturan perangkat,
token, langganan, marketplace, forum, laporan. BUKAN app anak
(itu repo `ZX-Parenting-agent`).

Backend: Next.js + Prisma + PostgreSQL di repo `djvpri/zx_parenting`,
deploy Coolify di `zxparenting.zomet.my.id`. Web = admin only. Ortu+anak
= APK only.

## Quick Reference

```bash
# Build debug (verifikasi tercepat, ~40s)
export JAVA_HOME="C:\Users\KBK065\jdk\jdk-17.0.20.1+1"
export PATH="/c/Users/KBK065/jdk/jdk-17.0.20.1+1/bin:$PATH"
cd C:\Users\KBK065\ZX-Parenting-ortu
./gradlew --no-daemon assembleDebug

# Health check (i18n, error handling, API mapping)
bash scripts/cek.sh

# Rilis (trigger via GitHub API)
# Lihat section "Rilis Workflow" di bawah
```

## Struktur Repo

```
ZX-Parenting-ortu/
├── app/src/main/java/com/zxparenting/ortu/
│   ├── MainActivity.kt          # Entry point, nav host, crash handler (457 LOC)
│   ├── LaporError.kt            # Crash report ke backend (fire-and-forget)
│   ├── PemeriksaPembaruan.kt    # Auto-update dari GitHub Releases
│   ├── ManajerBahasa.kt         # Toggle EN/ID (DataStore)
│   ├── api/
│   │   ├── Api.kt               # Retrofit interface + data class (774 LOC, 49 endpoints)
│   │   └── Klien.kt             # Retrofit builder, base URL dari alamat.json
│   ├── data/
│   │   └── Simpanan.kt          # DataStore: TOKEN, NAMA, USER_ID
│   └── ui/
│       ├── ZxVm.kt              # ViewModel — semua API call (902 LOC, terbesar)
│       ├── ZxVmFactory.kt       # ViewModel factory
│       ├── layar/               # 18 Composable screen
│       │   ├── LayarLogin.kt    # Login (email+PW, Google Sign-In)
│       │   ├── LayarDaftar.kt   # Register
│       │   ├── LayarBeranda.kt  # Dashboard ringkasan
│       │   ├── LayarAnak.kt     # CRUD profil anak (487 LOC)
│       │   ├── LayarTugas.kt    # Tugas + riwayat + AI generate (611 LOC)
│       │   ├── LayarAturan.kt   # Aturan perangkat (screen-time, curfew)
│       │   ├── LayarLaporan.kt  # Laporan aktivitas anak
│       │   ├── LayarProfil.kt   # Profil ortu, logout, hapus akun
│       │   ├── DetailRiwayatSheet.kt  # Bottom sheet review jawaban
│       │   └── ... (Coin, Forum, Marketplace, Quest, dll)
│       └── tema/                # Theme system
│           ├── Palet.kt         # Warna dasar (Oranye/Pink/Teal/Muted)
│           ├── Tema.kt          # Material3 theme
│           ├── Komponen.kt      # Reusable Compose components
│           ├── Teks.kt          # Typography
│           └── PengaturanTema.kt
├── app/src/main/res/
│   ├── values/strings.xml       # i18n EN (159 strings)
│   ├── values-in/strings.xml    # i18n ID (159 strings)
│   └── AndroidManifest.xml
├── alamat.json                  # Versi app (versiNama, versiKode), URL, idPaket
├── .github/workflows/
│   ├── ci.yml                   # CI: assembleDebug on push
│   └── rilis.yml                # Release: workflow_dispatch, build AAB+APK
└── scripts/
    └── cek.sh                   # Health check script
```

## File Penting — Prioritas Saat Edit

| File | LOC | Edit untuk |
|------|-----|------------|
| `ui/ZxVm.kt` | 902 | Semua API call, business logic, state management |
| `api/Api.kt` | 774 | Tambah/ubah Retrofit endpoint, data class |
| `ui/layar/LayarTugas.kt` | 611 | UI tugas, riwayat, AI generate |
| `ui/layar/LayarAnak.kt` | 487 | CRUD profil anak |
| `MainActivity.kt` | 457 | Nav, crash handler, theme init |
| `ui/layar/LayarJadwalAi.kt` | 359 | Jadwal tugas AI otomatis |
| `ui/layar/LayarLaporan.kt` | 287 | Laporan aktivitas |
| `ui/layar/DetailRiwayatSheet.kt` | 281 | Review jawaban kuis |

## Convention

### i18n — WAJIB `stringResource`

Setiap string yang tampil ke user HARUS pakai `stringResource(R.string.key)`.
Dua file: `values/strings.xml` (EN) + `values-in/strings.xml` (ID). Count
harus sama.

**Dilarang:** `Text("Selesai")` → hardcoded bypass i18n.

**Benar:** `Text(stringResource(R.string.status_selesai))`

Audit hardcoded: `grep -rn 'Text("[A-Z]' app/src/main/java --include="*.kt"`

### Error Handling

- `LaporError.kirim(ctx, screen, message)` — kirim crash report ke backend
- `catch (e: Exception)` dengan `e` → report error ke UI
- `catch (_: Exception) {}` — **SILENT SWALLOW**. 25 lokasi di ZxVm.kt.
  Saat fix bug, cek apakah swallow ini menyembunyikan gejala.

### Compose Pattern

- `MaterialTheme.colorScheme.*` untuk warna semantic
- `WarnaAktif.palet.*` untuk warna brand (Oranye/Pink/Teal)
- `Surface(onClick=...)` valid Material3 — tidak perlu wrap `Clickable`
- LazyColumn: Card di scope LazyColumn HARUS dibungkus `item { }`

### Retrofit

- Endpoint annotation: `@GET`, `@POST`, `@PATCH`, `@DELETE`
- Bearer token: `@Header("Authorization") bearer: String`
- Response: pakai `Response<T>` wrapper, cek `res.isSuccessful` + `res.body()`
- Base URL dari `alamat.json` → `Klien.kt`

## Rilis Workflow

```bash
# 1. Bump versi di alamat.json
#    versiNama: "3.7.1" → "3.8.0" (minor) atau "3.7.2" (patch)
#    versiKode: 63 → 64 (CI akan auto-increment lagi, tapi set manual)

# 2. Commit + push
git add alamat.json && git commit -m "rilis vX.Y.Z (versiKode N)"
git pull --rebase origin main  # CI auto-bump versiKode, resolve conflict
git push origin main

# 3. Trigger rilis.yml via GitHub API
TOKEN=$(printf "protocol=https\nhost=github.com\n" | git credential fill 2>/dev/null | grep password= | cut -d= -f2)
curl -s -X POST -H "Authorization: token $TOKEN" \
  "https://api.github.com/repos/djvpri/ZX-Parenting-ortu/actions/workflows/rilis.yml/dispatches" \
  -d '{"ref":"main","inputs":{"versi":"X.Y.Z"}}'

# 4. Cek status (3-5 menit)
curl -s -H "Authorization: token $TOKEN" \
  "https://api.github.com/repos/djvpri/ZX-Parenting-ortu/actions/runs?per_page=2"

# 5. Cek release
curl -s -H "Authorization: token $TOKEN" \
  "https://api.github.com/repos/djvpri/ZX-Parenting-ortu/releases/latest"
```

**Pitfall:** CI `rilis.yml` auto-increment `versiKode` tiap rilis. Saat
`git pull --rebase`, conflict di `alamat.json` = ambil versi remote (lebih
tinggi). Pakai `GIT_EDITOR=true git pull --rebase origin main` di MSYS.

## Versi History

| Versi | Code | Tanggal | Fitur |
|-------|------|---------|-------|
| 3.7.1 | 63 | 2026-10-13 | V2 app bar gradient header (LayarTugas) |
| 3.7.0 | 61 | 2026-10-13 | i18n fix — hardcoded status → stringResource |
| 3.6.0 | 60 | 2026-10-13 | LaporError crash handler + ManajerBahasa |
| 3.5.0 | 58 | 2026-10-12 | Fix tombol keluar terpotong |
| 3.2.0 | 45 | 2026-10-09 | Riwayat tugas clickable + detail sheet |

## Pitfalls (Terbukti)

- **`GIT_EDITOR=true` untuk rebase di MSYS.** `git pull --rebase` hang tanpa
  ini. Prefix semua rebase command.
- **CI auto-bump versiKode.** Remote `alamat.json` punya versiKode lebih
  tinggi setelah rilis. Cek remote sebelum push.
- **Keystore rotation v3.4.2.** Signature beda dengan pre-v3.4.2. User
  harus uninstall versi lama sebelum install baru.
- **`catch (_: Exception) {}` swallow error.** 25 lokasi di ZxVm.kt.
  Saat debugging API yang tidak respons, cek lokasi ini.
- **Hardcoded string masih ada.** DetailRiwayatSheet, LayarAnak, LayarAturan
  masih punya `Text("...")` literal. Audit: `grep -rn 'Text("[A-Z]'`.
- **Local build = verify tercepat.** `assembleDebug` ~40s vs CI ~3min.
  Selalu build lokal sebelum push.
- **Google Sign-In project.** Web Client ID di project `zxparenting`
  (`117197293834-...`). Android OAuth client di project yang SAMA, bukan
  `z-one-500223`.
- **Retrofit import.** Saat tambah endpoint baru, cek semua annotation
  import (`GET`, `POST`, `PATCH`, `DELETE`, `Header`, `Body`, `Path`,
  `Query`). Existing code pakai fully-qualified inline yang compile tanpa
  import — jangan ikuti, import eksplisit.

## Backend API

Lihat `docs/api-contract.md` untuk daftar lengkap endpoint, request/response
shape, dan mapping ke file Kotlin.

Backend repo: `djvpri/zx_parenting` (PRIVATE). Clone lokal:
`C:\Users\KBK065\zx_parenting`. Deploy: Coolify auto-deploy dari git push.
Start cmd: `npx prisma db push && npm start` (auto schema sync tiap deploy).
