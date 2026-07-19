# Product Requirements Document (PRD)
## Todo List App — Android

**Versi:** 1.0
**Platform:** Android (native)

---

## 1. Latar Belakang

Aplikasi todo list untuk membantu pengguna mengatur dan melacak tugas harian secara sederhana, cepat, dan sepenuhnya offline. Target awal adalah penggunaan personal per perangkat tanpa kebutuhan sinkronisasi lintas device atau akun pengguna.

## 2. Tujuan Produk

- Memberikan cara cepat mencatat, mengelola, dan menyelesaikan tugas harian.
- Mengurangi friksi input — menambah task harus terasa instan.
- Memberikan gambaran visual jelas soal prioritas dan tenggat waktu.
- Berjalan sepenuhnya offline tanpa ketergantungan koneksi internet.

## 3. Target Pengguna

- Individu yang butuh pengelola tugas pribadi sederhana.
- Pengguna yang lebih menyukai aplikasi ringan dan offline dibanding solusi cloud yang kompleks.

## 4. Ruang Lingkup (Scope)

### Termasuk (v1.0)
- Tambah, edit, hapus task
- Tandai task selesai/belum selesai
- Prioritas task (Tinggi/Sedang/Rendah)
- Kategori task (custom, dibuat user)
- Tanggal jatuh tempo & waktu pengingat
- Subtugas (checklist di dalam task)
- Filter (Semua, Hari ini, Mendatang)
- Statistik ringkas (jumlah selesai, jumlah prioritas tinggi)
- Notifikasi pengingat lokal
- Mode gelap (dark mode)

### Tidak termasuk (v1.0)
- Login / akun pengguna
- Sinkronisasi antar perangkat
- Kolaborasi/berbagi task ke pengguna lain
- Backup otomatis ke cloud
- Widget home screen (dipertimbangkan untuk v2)

## 5. User Stories

| # | Sebagai... | Saya ingin... | Supaya... |
|---|---|---|---|
| 1 | Pengguna | menambah task dengan cepat | tidak lupa mencatat hal yang perlu dikerjakan |
| 2 | Pengguna | menandai task selesai | bisa melacak progres harian |
| 3 | Pengguna | mengatur prioritas task | fokus ke hal yang paling penting dulu |
| 4 | Pengguna | mengelompokkan task ke kategori | mengatur task berdasarkan konteks (kerja, pribadi, dll) |
| 5 | Pengguna | menerima pengingat sebelum jatuh tempo | tidak melewatkan tenggat waktu |
| 6 | Pengguna | melihat subtugas dalam satu task | memecah pekerjaan besar jadi langkah kecil |
| 7 | Pengguna | memfilter task berdasarkan waktu | melihat task yang relevan hari ini |

## 6. Fitur & Requirement Detail

### 6.1 Manajemen Task
- Setiap task memiliki: judul (wajib), deskripsi (opsional), tanggal jatuh tempo, waktu pengingat, prioritas, kategori, status selesai, dan daftar subtugas.
- Task bisa diedit dan dihapus kapan saja.
- Menghapus task menampilkan konfirmasi sebelum eksekusi.

### 6.2 Prioritas
- Tiga level: Tinggi, Sedang, Rendah.
- Ditampilkan sebagai badge warna di list (merah, kuning, hijau).

### 6.3 Kategori
- Kategori default: Kerja, Pribadi, Belanja.
- Pengguna bisa menambah kategori baru secara bebas.

### 6.4 Subtugas
- Task bisa memiliki 0 atau lebih subtugas.
- Progres subtugas ditampilkan sebagai pecahan (contoh: 2/4) di layar detail.

### 6.5 Filter & Pencarian
- Filter cepat: Semua, Hari ini, Mendatang.
- (v1.1) Pencarian task berdasarkan judul.

### 6.6 Notifikasi
- Pengingat lokal dikirim sesuai waktu yang diset di task.
- Notifikasi bisa langsung membuka detail task saat diketuk.

### 6.7 Statistik
- Ringkasan jumlah task selesai vs total, dan jumlah task prioritas tinggi, ditampilkan di layar utama.

## 7. Non-Functional Requirements

| Aspek | Requirement |
|---|---|
| Performa | Aplikasi tetap responsif dengan hingga 1000 task tersimpan |
| Offline | Semua fitur inti berfungsi tanpa koneksi internet |
| Penyimpanan | Data tersimpan lokal menggunakan database Room (SQLite) |
| Kompatibilitas | Minimum Android 8.0 (API 26) ke atas |
| Aksesibilitas | Kontras warna memenuhi standar WCAG AA, ukuran tap target minimal 44dp |

## 8. Arsitektur Teknis (Ringkasan)

- **Bahasa:** Kotlin
- **UI Framework:** Jetpack Compose
- **Database lokal:** Room (SQLite)
- **Arsitektur:** MVVM (Model-View-ViewModel)
- **Reminder:** WorkManager / AlarmManager untuk notifikasi terjadwal
- **Tanpa backend** — semua data disimpan per perangkat

## 9. Metrik Keberhasilan

- Waktu rata-rata untuk menambah task baru < 5 detik.
- Tingkat penyelesaian task mingguan (task selesai / task dibuat).
- Retensi pengguna harian (jumlah hari aplikasi dibuka per minggu).

## 10. Risiko & Batasan

| Risiko | Mitigasi |
|---|---|
| Data hilang jika HP rusak/hilang (tanpa backup) | Informasikan batasan ini secara jelas ke user; pertimbangkan fitur export/import manual di v1.1 |
| Notifikasi tidak muncul karena battery optimization Android | Edukasi user untuk whitelist aplikasi dari battery optimization |

## 11. Roadmap Singkat

- **v1.0** — Fitur inti (task, prioritas, kategori, subtugas, filter, notifikasi, dark mode)
- **v1.1** — Pencarian task, export/import data manual (backup lokal ke file)
- **v2.0** — Widget home screen, opsi sinkronisasi (jika ada demand)

## Additional 

Buatkan Github Actions nya juga untuk pipeline build app nya lalu masukan ke Releases untuk kita download app/apk nya, dan dibuat pipeline jalan kalau kita bikin tag. Contoh 1.0.0

---

*Dokumen ini adalah draft awal dan dapat berubah seiring proses desain dan development.*