# API Contract — ZX-Parenting-Ortu

Mapping endpoint Retrofit → ViewModel function → UI Layar.
Base URL: `https://zxparenting.zomet.my.id/api`
Auth: `Authorization: Bearer <jwt>` (kecuali login/daftar)

## Auth

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| POST | `auth/native-login` | `login()` | `LoginReq(email, password)` | `LoginRes(token, user)` | LayarLogin |
| POST | `auth/native-daftar` | `daftar()` | `DaftarReq(nama, email, password, setuju)` | `LoginRes` | LayarDaftar |
| POST | `auth/native-google` | `loginGoogle()` | `GoogleReq(idToken, audience)` | `LoginRes` | LayarLogin |

## Anak

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `anak` | `muatAnak()` | — | `List<Anak>` | LayarAnak, LayarBeranda |
| POST | `anak` | `buatAnak()` | `AnakCreateReq(nama, username, pin, tanggalLahir?, kelas?, gender?, agama?)` | `AnakCreateRes(ok, anakId)` | LayarAnak |
| GET | `anak/{id}` | — | — | `AnakDetailRes` | (unused in VM?) |
| PATCH | `anak/{id}` | `editAnak()` | `AnakUpdateReq(nama?, username?, tanggalLahir?, kelas?, gender?, agama?)` | `AnakOkRes(ok)` | LayarAnak |
| DELETE | `anak/{id}` | `hapusAnak()` | — | `AnakOkRes(ok)` | LayarAnak |
| PATCH | `anak/{id}/pin` | `resetPinAnak()` | `AnakResetPinReq(pin)` | `AnakOkRes(ok)` | LayarAnak |

**Anak fields:** `id, nama, umur?, tanggalLahir?, username?, kelas?, gender?, agama?, tokenBalance{balance}, dormant`

## Tugas

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `tugas` | `muatTugas()` | — | `List<Tugas>` | LayarTugas |
| POST | `tugas` | `buatTugas()` | `TugasCreateReq(anakId, judul, deskripsi?, tokenReward, deadline?)` | `TugasCreateRes(ok, tugasId)` | LayarTugas |
| POST | `tugas/generate` | `generateTugas()` | `TugasGenerateReq(anakId, tema, kesulitan, tokenReward, maxRetry, timerMenit?, autoApprove, jenisSoal, jumlahSoal)` | `TugasGenerateRes(ok, saran[], meta)` | LayarTugas |
| POST | `tugas/batch` | `batchTugas()` | `TugasBatchReq(anakId, tema, kesulitan, tokenReward, maxRetry, timerMenit?, autoApprove, jenisSoal, soal[])` | `TugasBatchRes(ok, id?)` | LayarTugas |
| PATCH | `tugas/{id}` | `validasiTugas()` | `TugasValidasiReq(aksi: "selesai"\|"tolak")` | `TugasOkRes(ok)` | LayarTugas |
| GET | `tugas/{id}/review` | — | — | `TugasReviewRes` | DetailRiwayatSheet |

**Tugas fields:** `id, judul, deskripsi?, tokenReward, status, fotoBuktiUrl?, validasiOrtu?, deadline?, createdAt, anak{id,nama}, type, tokenDiklaim?, waktuExpire?`

**TugasReviewRes:** `adaReview, sessionId?, skor?, benarCount?, totalSoal?, durasiDetik?, totalAttempt?, tugas{judul,anak,type}, review[]{posisi,question,options[],jawabanAnak?,jawabanBenar,benar,explanation}`

## Device Session (Aturan Perangkat)

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `device-session` | `muatDevices()` | — | `List<Device>` | LayarAturan |
| PATCH | `device-session/{id}` | `patchDevice()` | `DevicePatch(sessionLimit?, cooldown?, dailyLimit?, curfewMulai?, curfewSelesai?, masterUnlock?, gpsAktif?, geoAktif?, geoLat?, geoLng?, geoRadius?, appBlokir?)` | `Device` | LayarAturan |
| DELETE | `device-session/{id}` | `hapusDevice()` | — | `TugasOkRes(ok)` | LayarAturan |

## Hadiah / Marketplace

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `hadiah` | `muatMarket()` | — | `HadiahMarketRes(hadiah[], pesanan[])` | LayarMarketplace |
| POST | `hadiah` | `buatHadiah()` | `HadiahCreateReq(judul, deskripsi?, hargaCoin, stok)` | `Hadiah` | LayarMarketplace |
| PATCH | `hadiah` | — | `HadiahPatchReq(id, aktif?, stok?, hargaCoin?)` | `Hadiah` | LayarMarketplace |
| DELETE | `hadiah` | `hapusHadiah()` | `?id=<id>` | `TugasOkRes(ok)` | LayarMarketplace |
| POST | `hadiah/pesanan` | `prosesPesanan()` | `PesananProsesReq(id, aksi: "beli"\|"tolak")` | `PesananProsesRes(ok, status, saldo?)` | LayarMarketplace |

## Coin

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `coin` | `muatCoin()` | — | `CoinRes(saldo, ledger[])` | LayarCoin |
| POST | `coin` | `topupCoin()` | `CoinTopupReq(aksi, jumlah)` | `CoinSaldoRes(saldo)` | LayarCoin |

## Quest

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `quests` | `muatQuest()` | — | `List<Quest>` | LayarQuest |
| POST | `quests` | `buatQuest()` | `QuestCreateReq(judul, deskripsi?, tokenReward, deadline?)` | `Quest` | LayarQuest |

## Langganan

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `langganan/status` | `muatLangganan()` | — | `LanggananRes(tier, mulai, berakhir, hariSisa, aktif, limitAnak)` | LayarLangganan |

## Pesan (Chat)

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `pesan` | `muatPesan()` | — | `List<PesanRingkas>` | LayarPesan |
| GET | `pesan?with={userId}` | `muatThread()` | `?with=<userId>` | `List<Pesan>` | LayarPesan |
| POST | `pesan` | `kirimPesan()` | `PesanKirimReq(penerimaId, isi)` | `Pesan` | LayarPesan |

## Insights

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `insights?anakId={id}` | `muatInsights()` | `?anakId=<id>` | `InsightsRes(anakNama, periode, insights[], ringkasan, sumber?)` | LayarInsights |

## Laporan

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `laporan/ringkasan?anakId={id}` | `muatLaporan()` | `?anakId=<id>` | `LaporanRingkasanRes(anakNama, kartu, tugasTerbaru[], laporanKuis[], tokenStat)` | LayarLaporan |

## Aktivitas

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `aktivitas?anakId={id}&limit={n}` | `muatAktivitas()` | `?anakId=<id>&limit=50` | `AktivitasRes(anak, logs[], ringkasan7hari[])` | LayarLaporan |

## Pinjam Waktu

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `pinjam?anakId={id}` | `muatPinjam()` | `?anakId=<id>` | `PinjamRes(riwayat[], hutang)` | LayarPinjam |

## Referral

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `referral` | `muatReferral()` | — | `ReferralRes(kode, totalReferral, totalBonusCoin, riwayat[])` | LayarReferral |
| POST | `referral` | `klaimReferral()` | `ReferralClaimReq(kode)` | `ReferralRes` | LayarReferral |

## Leaderboard

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `leaderboard?scope={scope}` | `muatLeaderboard()` | `?scope=global` | `LeaderboardRes(ranking[], periode, scope)` | LayarLeaderboard |

## Daily Challenges

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `challenges?anakId={id}` | — | `?anakId=<id>` | `ChallengesRes(challenges[], tanggal)` | LayarChallenges |
| POST | `challenges` | — | `ChallengeClaimReq(anakId, challengeId)` | `ChallengeClaimRes(ok, reward, nama)` | LayarChallenges |

## Forum

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `forum` | `muatForum()` | — | `List<ForumPost>` | LayarForum |
| POST | `forum` | `buatPost()` | `ForumCreateReq(judul, isi, kategori)` | `ForumPost` | LayarForum |
| GET | `forum/{id}` | `muatForumDetail()` | — | `ForumPost` (with komentar) | LayarForum |
| DELETE | `forum/{id}` | `hapusPost()` | — | `TugasOkRes(ok)` | LayarForum |
| POST | `forum/{id}/komentar` | `tambahKomentar()` | `KomentarReq(isi)` | `ForumKomentar` | LayarForum |

## Jadwal Tugas AI

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| GET | `jadwal-tugas-ai` | `muatJadwalAi()` | — | `List<JadwalTugasAI>` | LayarJadwalAi |
| POST | `jadwal-tugas-ai` | `buatJadwalAi()` | `JadwalAiCreateReq(anakIds[], tema, kesulitan, jamKirim, hariAktif[], tokenReward, autoApprove, maxRetry, timerMenit?)` | `JadwalTugasAI` | LayarJadwalAi |
| PATCH | `jadwal-tugas-ai/{id}` | `toggleJadwalAi()` | `JadwalAiUpdateReq(...)` | `JadwalTugasAI` | LayarJadwalAi |
| DELETE | `jadwal-tugas-ai/{id}` | `hapusJadwalAi()` | — | `TugasOkRes(ok)` | LayarJadwalAi |

## Akun

| Method | Endpoint | VM Function | Request | Response | Layar |
|--------|----------|-------------|---------|----------|-------|
| DELETE | `akun/hapus` | `hapusAkun()` | — | `TugasOkRes(ok)` | LayarProfil |

## Lapor Error (App → Backend)

| Method | Endpoint | Caller | Request | Response |
|--------|----------|--------|---------|----------|
| POST | `/api/ortu/lapor-error` | `LaporError.kirim()` | `{ sumber, nama, screen, message, extra }` Bearer JWT | `{ ok: boolean }` |

## Error Handling Audit

### `LaporError.kirim()` call sites (1 total)
- `MainActivity.kt:96` — global crash handler (`Thread.setDefaultUncaughtExceptionHandler`)

### Silent swallow `catch (_: Exception) {}` (37 total, all in `ZxVm.kt`)
Daftar function yang swallow error diam-diam — saat debugging API yang
tidak respons, cek lokasi ini:

| Line | Function | Endpoint |
|------|----------|----------|
| 202 | `muatLangganan` | `langganan/status` |
| 214 | `muatDevices` | `device-session` |
| 226 | `muatAnak` | `anak` |
| 238 | `muatTugas` | `tugas` |
| 250 | `muatJadwalAi` | `jadwal-tugas-ai` |
| 295 | `toggleJadwalAi` | `jadwal-tugas-ai/{id}` |
| 307 | `hapusJadwalAi` | `jadwal-tugas-ai/{id}` |
| 319 | `muatAktivitas` | `aktivitas` |
| 333 | `muatMarket` | `hadiah` |
| 358 | `muatCoin` | `coin` |
| 373 | `muatQuest` | `quests` |
| 587 | `muatForum` | `forum` |
| 647 | `muatPesan` | `pesan` |
| 657 | `muatThread` | `pesan` |
| 668 | `kirimPesan` | `pesan` |
| 689 | `muatInsights` | `insights` |
| 716 | `muatLaporan` | `laporan/ringkasan` |
| 726 | `muatPinjam` | `pinjam` |
| 736 | `muatReferral` | `referral` |
| 754 | `muatLeaderboard` | `leaderboard` |
| 766+ | (batch operations) | various |

**Pattern:** Function dengan `catch (e: Exception)` (e dilaporkan) = login,
daftar, generateTugas, batchTugas, buatJadwalAi, buatAnak, editAnak,
resetPinAnak, hapusAnak, buatTugas, validasiTugas, patchDevice, hapusDevice,
hapusAkun, hapusPost, tambahKomentar, klaimReferral, buatHadiah,
hapusHadiah, prosesPesanan, topupCoin, buatQuest, buatPost.

**Rekomendasi:** Ganti `catch (_: Exception) {}` → `catch (e: Exception) {
state.value = state.value.copy(error = e.message) }` untuk fungsi `muat*`
agar error tampil ke UI, bukan swallow diam-diam.
