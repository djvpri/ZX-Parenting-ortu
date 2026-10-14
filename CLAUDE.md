# CLAUDE.md

## Quick Context

ZX-Parenting-Ortu: APK Android (Kotlin + Compose + Material 3) untuk orang tua.
Backend: Next.js + Prisma di `zxparenting.zomet.my.id`. Repo: `djvpri/ZX-Parenting-ortu` (PUBLIC).

**Versi:** 3.7.1 / code 63. **36 file Kotlin, 7887 LOC, 50 Retrofit endpoints.**

## 3 Commands

```bash
# Build debug
export JAVA_HOME="C:\Users\KBK065\jdk\jdk-17.0.20.1+1"
./gradlew --no-daemon assembleDebug

# Health check
bash scripts/cek.sh

# Rilis (lihat AGENTS.md untuk detail)
# bump alamat.json → commit → push → trigger rilis.yml via GitHub API
```

## Key Files

- `ui/ZxVm.kt` (902 LOC) — ViewModel, semua API call
- `api/Api.kt` (774 LOC) — Retrofit interface + data class
- `MainActivity.kt` (457 LOC) — Nav, crash handler
- `ui/layar/LayarTugas.kt` (611 LOC) — Tugas + riwayat + AI
- `alamat.json` — Versi app (sumber tunggal)

## Rules

- i18n wajib `stringResource(R.string.key)`. EN + ID count harus sama.
- `catch (_: Exception) {}` = silent swallow. 37 lokasi di ZxVm.kt.
- Build lokal dulu sebelum push. CI auto-bump versiKode.
- `GIT_EDITOR=true git pull --rebase origin main` di MSYS.
- Lihat `AGENTS.md` untuk detail lengkap, `docs/api-contract.md` untuk endpoint mapping.
