# Rapi — Todo List Android

Rapi adalah aplikasi pengelola tugas personal yang ringan, sepenuhnya offline, dan dibuat dengan Kotlin + Jetpack Compose.

## Fitur

- Tambah cepat, edit, selesaikan, dan hapus tugas
- Prioritas tinggi/sedang/rendah dan kategori custom
- Tenggat, pengingat lokal, dan subtugas
- Filter Semua, Hari ini, dan Mendatang
- Statistik ringkas dan mode gelap
- Penyimpanan lokal Room tanpa akun atau backend

## Menjalankan aplikasi

1. Buka proyek di Android Studio terbaru.
2. Tunggu proses sinkronisasi Gradle selesai.
3. Jalankan konfigurasi `app` pada emulator atau perangkat Android 8.0+.

Atau dari terminal dengan JDK 17 dan Android SDK 36:

```bash
gradle test assembleDebug
```

APK debug tersedia di `app/build/outputs/apk/debug/app-debug.apk`.

## Membuat rilis APK

Push tag versi tiga angka untuk menjalankan GitHub Actions dan membuat GitHub Release otomatis:

```bash
git tag 1.0.0
git push origin 1.0.0
```

APK pada GitHub Release ditandatangani dengan kunci debug agar dapat langsung dipasang untuk pengujian. Untuk distribusi Play Store, tambahkan signing key produksi melalui GitHub Secrets.
