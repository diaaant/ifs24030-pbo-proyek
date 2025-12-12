# My Travel Journal (Proyek Akhir PBO)

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)

## Deskripsi
**My Travel Journal** adalah aplikasi berbasis web yang dirancang untuk memenuhi Tugas Proyek Akhir mata kuliah **Pemrograman Berorientasi Objek (PBO)**. 

Aplikasi ini memungkinkan pengguna untuk mendokumentasikan pengalaman perjalanan wisata mereka secara digital. Pengguna dapat menyimpan cerita perjalanan, lokasi destinasi, total biaya, rating, serta mengunggah foto kenangan.

## Fitur Utama (Sesuai Rubrik Penilaian)
1.  **Clean Architecture:** Struktur proyek terpisah rapi (Entity, Repository, Service, Controller/View).
2.  **Authentication:** Sistem Login & Register aman menggunakan BCrypt & JWT.
3.  **CRUD Jurnal:**
    *   **Create:** Menambah catatan perjalanan baru.
    *   **Read:** Menampilkan daftar perjalanan (Card View) & Detail lengkap.
    *   **Update:** Mengubah data teks (Judul, Deskripsi, Biaya).
    *   **Delete:** Menghapus jurnal perjalanan.
4.  **Fitur Spesifik:**
    *   **Upload Gambar:** Pengguna dapat mengganti/mengupload foto kenangan perjalanan.
    *   **Search:** Pencarian berdasarkan Judul atau Lokasi.
    *   **Chart Data:** Grafik statistik pengeluaran biaya per destinasi wisata.
5.  **UI Menarik:** Menggunakan Bootstrap 5 & SweetAlert2 untuk tampilan yang responsif dan interaktif.

## Struktur Entitas (TravelLog)
Entitas utama `TravelLog` memiliki atribut lengkap:
*   `id` (UUID)
*   `userId` (UUID - Relasi ke User)
*   `title` (String)
*   `destination` (String)
*   `description` (Text)
*   `imagePath` (String - Path Gambar)
*   `totalCost` (Double - Untuk Chart)
*   `rating` (Integer - 1 s.d 5)
*   `createdAt` & `updatedAt`

## Cara Menjalankan Aplikasi

### Prasyarat
*   Java Development Kit (JDK) 17 atau lebih baru.
*   Maven.
*   PostgreSQL Database.

### Instalasi & Run
1.  **Clone Repository:**
    ```bash
    git clone https://github.com/username/travel-journal.git
    cd travel-journal
    ```

2.  **Konfigurasi Database:**
    Edit file `src/main/resources/application.properties` sesuai kredensial database lokal Anda:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/travel_db
    spring.datasource.username=postgres
    spring.datasource.password=password_anda
    ```

3.  **Jalankan Aplikasi:**
    ```bash
    mvn spring-boot:run
    ```

4.  **Akses Aplikasi:**
    Buka browser dan kunjungi: [http://localhost:8080](http://localhost:8080)

### Menjalankan Testing
Untuk memeriksa unit testing dan coverage (JaCoCo):
```bash
mvn test jacoco:report