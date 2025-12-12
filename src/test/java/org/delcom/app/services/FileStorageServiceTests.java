package org.delcom.app.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FileStorageServiceTests {

    private FileStorageService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new FileStorageService();
        // Default: set ke tempDir yang sudah ada
        service.uploadDir = tempDir.toString();
    }

    // --- 1. Test Create Directory (Menutup Baris 23 yang Merah) ---
    @Test
    @DisplayName("Store File: Harus membuat folder baru jika belum ada")
    void testStoreFile_CreatesDirectory() throws IOException {
        // Arrange
        // Arahkan uploadDir ke sub-folder yang BELUM ADA
        Path nonExistentPath = tempDir.resolve("folder-baru");
        service.uploadDir = nonExistentPath.toString();

        UUID id = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        // Act
        // Ini akan memicu if (!Files.exists(...)) -> Files.createDirectories(...)
        String result = service.storeFile(file, id);

        // Assert
        assertTrue(Files.exists(nonExistentPath), "Folder baru harusnya otomatis dibuat");
        assertTrue(result.endsWith(".jpg"));
    }

    // --- 2. Test Filename Null (Menutup Baris 29 yang Kuning) ---
    @Test
    @DisplayName("Store File: Filename Null (Edge Case)")
    void testStoreFile_NullFilename() throws IOException {
        UUID id = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        
        // Simulasikan file tanpa nama (null)
        when(file.getOriginalFilename()).thenReturn(null);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        // Act
        String result = service.storeFile(file, id);

        // Assert
        // Harusnya tidak error, dan ekstensi kosong
        assertTrue(result.startsWith("cover_"));
        assertFalse(result.contains(".")); // Tidak ada titik karena ekstensi kosong
    }

    @Test
    @DisplayName("Store File: Normal dengan Ekstensi")
    void testStoreFile_WithExtension() throws IOException {
        UUID id = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("photo.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        String res = service.storeFile(file, id);
        assertTrue(res.endsWith(".png"));
    }

    @Test
    @DisplayName("Store File: File Tanpa Ekstensi (Contoh: README)")
    void testStoreFile_NoExtension() throws IOException {
        UUID id = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("README"); // Tidak ada titik
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        String res = service.storeFile(file, id);
        assertFalse(res.endsWith(".")); 
    }

    @Test
    @DisplayName("Delete File: Sukses")
    void testDeleteFile_Success() throws IOException {
        String filename = "file.txt";
        Files.createFile(tempDir.resolve(filename));
        boolean result = service.deleteFile(filename);
        assertTrue(result);
    }

    @Test
    @DisplayName("Delete File: Gagal (File Tidak Ada)")
    void testDeleteFile_NotExists() {
        boolean result = service.deleteFile("ghost.txt");
        assertFalse(result);
    }

    @Test
    @DisplayName("Delete File: Exception (IOError)")
    void testDeleteFile_Exception() {
        // Mock static Files.class untuk melempar IOException
        try (MockedStatic<Files> files = mockStatic(Files.class)) {
            // Kita perlu mock Paths.get juga karena mockStatic Files kadang mengganggu loading kelas lain
            // Tapi untuk kasus deleteIfExists, cukup seperti ini:
            
            files.when(() -> Files.deleteIfExists(any(Path.class)))
                 .thenThrow(new IOException("Disk Error"));
            
            // Kita harus memastikan Paths.get(...) tidak error di dalam try block service
            // Karena service.deleteFile memanggil Paths.get(uploadDir).resolve(...)
            // Mock static Files tidak mengganggu Paths.get
            
            boolean res = service.deleteFile("any.txt");
            
            // Harusnya return false (masuk catch block)
            assertFalse(res);
        }
    }

    @Test
    @DisplayName("File Exists & Load")
    void testFileExistsAndLoad() throws IOException {
        String filename = "cek.txt";
        Files.createFile(tempDir.resolve(filename));
        
        assertTrue(service.fileExists(filename));
        assertNotNull(service.loadFile(filename));
        
        assertFalse(service.fileExists("gaada.txt"));
    }
}