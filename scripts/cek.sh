#!/usr/bin/env bash
# cek.sh — Health check untuk repo ZX-Parenting-ortu
# Jalankan: bash scripts/cek.sh
# Exit 0 = semua PASS, exit 1 = ada FAIL
set -euo pipefail
cd "$(dirname "$0")/.."

PASS=0; FAIL=0; WARN=0

ok()   { echo "  ✅ PASS: $1"; PASS=$((PASS+1)); }
fail() { echo "  ❌ FAIL: $1"; FAIL=$((FAIL+1)); }
warn() { echo "  ⚠️  WARN: $1"; WARN=$((WARN+1)); }

echo "═══════════════════════════════════════════════"
echo "  ZX-Parenting-Ortu Health Check"
echo "═══════════════════════════════════════════════"
echo ""

# ── 1. i18n consistency ─────────────────────────
echo "── i18n ──"
EN=$(grep -c 'string name' app/src/main/res/values/strings.xml)
ID=$(grep -c 'string name' app/src/main/res/values-in/strings.xml)
if [ "$EN" = "$ID" ]; then
  ok "String count match: EN=$EN ID=$ID"
else
  fail "String count mismatch: EN=$EN ID=$ID"
fi

# Hardcoded string audit
HARDCODED=$(grep -rn 'Text("[A-Z]' app/src/main/java --include="*.kt" | grep -v 'import\|//\|stringResource\|R.string\|@Composable' | wc -l)
if [ "$HARDCODED" -eq 0 ]; then
  ok "No hardcoded Text() strings"
else
  warn "$HARDCODED hardcoded Text() strings found (should use stringResource)"
  grep -rn 'Text("[A-Z]' app/src/main/java --include="*.kt" | grep -v 'import\|//\|stringResource\|R.string\|@Composable' | head -5 | sed 's/^/      /'
fi

echo ""

# ── 2. Error handling ──────────────────────────
echo "── Error Handling ──"
LAPOR=$(grep -rn 'LaporError.kirim' app/src/main/java --include="*.kt" | grep -v 'LaporError.kt' | wc -l)
if [ "$LAPOR" -ge 1 ]; then
  ok "LaporError.kirim call sites: $LAPOR"
else
  warn "No LaporError.kirim calls outside definition file"
fi

SILENT=$(grep -rn 'catch (_: Exception)' app/src/main/java --include="*.kt" | wc -l)
warn "Silent swallow catch blocks: $SILENT (in ZxVm.kt — check if hiding bugs)"

echo ""

# ── 3. API endpoint coverage ───────────────────
echo "── API Endpoints ──"
ENDPOINTS=$(grep -c '@GET\|@POST\|@PATCH\|@DELETE' app/src/main/java/com/zxparenting/ortu/api/Api.kt)
ok "Retrofit endpoints defined: $ENDPOINTS"

echo ""

# ── 4. Build config ────────────────────────────
echo "── Build Config ──"
if [ -f alamat.json ]; then
  ok "alamat.json exists"
  VERSI=$(python3 -c "import json;print(json.load(open('alamat.json'))['versiNama'])" 2>/dev/null || echo "?")
  KODE=$(python3 -c "import json;print(json.load(open('alamat.json'))['versiKode'])" 2>/dev/null || echo "?")
  ok "Version: $VERSI / code $KODE"
else
  fail "alamat.json missing"
fi

if [ -f app/build.gradle.kts ]; then
  ok "build.gradle.kts exists"
else
  fail "build.gradle.kts missing"
fi

echo ""

# ── 5. Kotlin file count ───────────────────────
echo "── Codebase ──"
KT=$(find app/src/main/java -name "*.kt" | wc -l)
LOC=$(find app/src/main/java -name "*.kt" -exec cat {} + | wc -l)
ok "Kotlin files: $KT, total LOC: $LOC"

echo ""

# ── Summary ────────────────────────────────────
echo "═══════════════════════════════════════════════"
echo "  PASS=$PASS  WARN=$WARN  FAIL=$FAIL"
echo "═══════════════════════════════════════════════"

if [ "$FAIL" -gt 0 ]; then
  exit 1
else
  exit 0
fi
