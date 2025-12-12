package org.delcom.app.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTests {

    @Test
    @DisplayName("Test Constructor 3 Parameter (Full)")
    void testFullConstructor() {
        // Arrange
        String name = "Budi Santoso";
        String email = "budi@example.com";
        String password = "rahasia123";

        // Act
        User user = new User(name, email, password);

        // Assert
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    @DisplayName("Test Constructor 2 Parameter (Chaining Logic)")
    void testChainedConstructor() {
        // Arrange
        String email = "agus@example.com";
        String password = "passwordAgus";

        // Act
        // Kode Main Anda: public User(String email, String password) { this("", email, password); }
        User user = new User(email, password);

        // Assert
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        
        // Memastikan logic 'this("", ...)' berjalan
        assertEquals("", user.getName(), "Nama harus empty string sesuai logic konstruktor");
    }

    @Test
    @DisplayName("Test Setters dan Getters")
    void testSettersAndGetters() {
        // Arrange
        User user = new User();
        UUID id = UUID.randomUUID();
        String name = "Citra";
        String email = "citra@test.com";
        String password = "pass";

        // Act
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        // Assert
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    @DisplayName("Test Lifecycle onCreate (@PrePersist)")
    void testOnCreate() {
        // Arrange
        User user = new User();

        // Act
        // Memanggil method protected onCreate() (bisa karena satu package)
        user.onCreate();

        // Assert
        // Kode Main: createdAt = now(); updatedAt = now();
        assertNotNull(user.getCreatedAt(), "CreatedAt harus terisi");
        assertNotNull(user.getUpdatedAt(), "UpdatedAt harus terisi");
        
        // Memastikan keduanya di-set di waktu yang (hampir) bersamaan
        assertEquals(user.getCreatedAt(), user.getUpdatedAt());
    }

    @Test
    @DisplayName("Test Lifecycle onUpdate (@PreUpdate)")
    void testOnUpdate() throws InterruptedException {
        // Arrange
        User user = new User();
        user.onCreate(); // Set waktu awal
        LocalDateTime createdTime = user.getCreatedAt();
        LocalDateTime oldUpdatedTime = user.getUpdatedAt();

        // Tunggu sebentar (simulasi jeda waktu)
        Thread.sleep(10);

        // Act
        // Memanggil method protected onUpdate()
        user.onUpdate();

        // Assert
        // Kode Main: updatedAt = LocalDateTime.now(); (createdAt tidak disentuh)
        assertEquals(createdTime, user.getCreatedAt(), "CreatedAt tidak boleh berubah saat update");
        assertNotEquals(oldUpdatedTime, user.getUpdatedAt(), "UpdatedAt harus berubah");
        assertTrue(user.getUpdatedAt().isAfter(oldUpdatedTime), "UpdatedAt baru harus lebih besar");
    }
}