package org.delcom.app.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthTokenTests {

    @Test
    @DisplayName("Test Constructor dengan Parameter")
    void testParameterizedConstructor() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String tokenStr = "eyJhbGciOiJIUzI1NiJ9.sample.token";

        // Act
        // Konstruktor ini di kode main Anda otomatis mengisi createdAt = LocalDateTime.now()
        AuthToken authToken = new AuthToken(userId, tokenStr);

        // Assert
        assertEquals(userId, authToken.getUserId());
        assertEquals(tokenStr, authToken.getToken());
        assertNotNull(authToken.getCreatedAt(), "CreatedAt harus otomatis diisi oleh konstruktor");
    }

    @Test
    @DisplayName("Test Setters dan Getters")
    void testSettersAndGetters() {
        // Arrange
        AuthToken authToken = new AuthToken();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String token = "new-token-123";

        // Act
        authToken.setId(id);
        authToken.setUserId(userId);
        authToken.setToken(token);

        // Assert
        assertEquals(id, authToken.getId());
        assertEquals(userId, authToken.getUserId());
        assertEquals(token, authToken.getToken());
    }

    @Test
    @DisplayName("Test Lifecycle onCreate (@PrePersist)")
    void testOnCreate() {
        // Arrange
        AuthToken authToken = new AuthToken();
        
        // Sebelum onCreate dipanggil, createdAt mungkin null (jika pakai default constructor)
        // atau kita ingin memastikan nilainya diperbarui.

        // Act
        // Memanggil method protected onCreate() secara manual
        // Ini valid karena Test ini berada di package yang sama (org.delcom.app.entities)
        authToken.onCreate();

        // Assert
        assertNotNull(authToken.getCreatedAt(), "CreatedAt tidak boleh null setelah onCreate dipanggil");
        
        // Memastikan waktu yang di-set adalah waktu sekarang (toleransi 1-2 detik)
        LocalDateTime now = LocalDateTime.now();
        assertTrue(authToken.getCreatedAt().isBefore(now.plusSeconds(2)));
        assertTrue(authToken.getCreatedAt().isAfter(now.minusSeconds(2)));
    }
}