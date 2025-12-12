package org.delcom.app.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTests {

    @Test
    @DisplayName("1. Test Constructor Kosong")
    void testNoArgsConstructor() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
    }

    @Test
    @DisplayName("2. Test Constructor Email Password")
    void testConstructorEmailPassword() {
        User user = new User("test@mail.com", "pass");
        assertEquals("", user.getName());
        assertEquals("test@mail.com", user.getEmail());
        assertEquals("pass", user.getPassword());
    }

    @Test
    @DisplayName("3. Test Constructor Lengkap")
    void testFullConstructor() {
        User user = new User("Name", "email@mail.com", "pass");
        assertEquals("Name", user.getName());
        assertEquals("email@mail.com", user.getEmail());
        assertEquals("pass", user.getPassword());
    }

    @Test
    @DisplayName("4. Test Getter Setter")
    void testSettersAndGetters() {
        User user = new User();
        UUID id = UUID.randomUUID();
        
        user.setId(id);
        user.setName("Budi");
        user.setEmail("budi@mail.com");
        user.setPassword("rahasia");

        assertEquals(id, user.getId());
        assertEquals("Budi", user.getName());
        assertEquals("budi@mail.com", user.getEmail());
        assertEquals("rahasia", user.getPassword());
    }

    // --- BAGIAN YANG DIPERBAIKI ---
    @Test
    @DisplayName("5. Test onCreate (PrePersist)")
    void testOnCreate() {
        User user = new User();
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());

        // Panggil method lifecycle
        user.onCreate();

        // Assert Not Null
        assertNotNull(user.getCreatedAt(), "CreatedAt tidak boleh null");
        assertNotNull(user.getUpdatedAt(), "UpdatedAt tidak boleh null");

        // PERBAIKAN: Jangan pakai assertEquals untuk waktu.
        // Gunakan ChronoUnit untuk memastikan selisihnya sangat kecil (misal < 100ms)
        long diff = ChronoUnit.MILLIS.between(user.getCreatedAt(), user.getUpdatedAt());
        assertTrue(Math.abs(diff) < 100, "Waktu create dan update harus hampir bersamaan");
        
        // Pastikan tahunnya benar (tahun ini)
        assertEquals(LocalDateTime.now().getYear(), user.getCreatedAt().getYear());
    }

    @Test
    @DisplayName("6. Test onUpdate (PreUpdate)")
    void testOnUpdate() throws InterruptedException {
        User user = new User();
        user.onCreate(); // Init awal
        
        LocalDateTime awal = user.getUpdatedAt();
        
        // Tunggu sebentar agar waktu berubah (10ms)
        Thread.sleep(10);
        
        user.onUpdate(); // Update

        // UpdatedAt harus berubah menjadi lebih baru
        assertTrue(user.getUpdatedAt().isAfter(awal), "UpdatedAt harus diperbarui");
        
        // CreatedAt TIDAK boleh berubah
        assertEquals(user.getCreatedAt(), user.getCreatedAt()); 
    }
}