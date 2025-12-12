package org.delcom.app.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TravelLogTests {

    @Test
    @DisplayName("Test Constructor Parameter")
    void testParameterizedConstructor() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String title = "Liburan Keluarga";
        String destination = "Yogyakarta";
        String description = "Jalan-jalan ke Malioboro";
        Double totalCost = 2500000.0;
        Integer rating = 5;

        // Act
        TravelLog log = new TravelLog(userId, title, destination, description, totalCost, rating);

        // Assert
        assertEquals(userId, log.getUserId());
        assertEquals(title, log.getTitle());
        assertEquals(destination, log.getDestination());
        assertEquals(description, log.getDescription());
        assertEquals(totalCost, log.getTotalCost());
        assertEquals(rating, log.getRating());
    }

    @Test
    @DisplayName("Test Setters dan Getters")
    void testSettersAndGetters() {
        // Arrange
        TravelLog log = new TravelLog();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String imagePath = "/uploads/img1.jpg";

        // Act
        log.setId(id);
        log.setUserId(userId);
        log.setTitle("Trip Solo");
        log.setDestination("Solo");
        log.setDescription("Kuliner enak");
        log.setImagePath(imagePath);
        log.setTotalCost(150000.0);
        log.setRating(4);

        // Assert
        assertEquals(id, log.getId());
        assertEquals(userId, log.getUserId());
        assertEquals("Trip Solo", log.getTitle());
        assertEquals("Solo", log.getDestination());
        assertEquals("Kuliner enak", log.getDescription());
        assertEquals(imagePath, log.getImagePath());
        assertEquals(150000.0, log.getTotalCost());
        assertEquals(4, log.getRating());
    }

    @Test
    @DisplayName("Test Lifecycle onCreate (@PrePersist)")
    void testOnCreate() {
        // Arrange
        TravelLog log = new TravelLog();

        // Act
        // Memanggil method protected onCreate() secara manual untuk simulasi
        log.onCreate();

        // Assert
        assertNotNull(log.getCreatedAt(), "CreatedAt harus terisi setelah onCreate");
        assertNotNull(log.getUpdatedAt(), "UpdatedAt harus terisi setelah onCreate");
        
        // Pastikan waktu yang diisi adalah waktu sekarang
        LocalDateTime now = LocalDateTime.now();
        assertTrue(log.getCreatedAt().isBefore(now.plusSeconds(1)));
        assertTrue(log.getCreatedAt().isAfter(now.minusSeconds(5)));
    }

    @Test
    @DisplayName("Test Lifecycle onUpdate (@PreUpdate)")
    void testOnUpdate() throws InterruptedException {
        // Arrange
        TravelLog log = new TravelLog();
        log.onCreate(); // Set waktu awal
        LocalDateTime initialUpdate = log.getUpdatedAt();

        // Beri jeda waktu sedikit agar terlihat perbedaannya
        Thread.sleep(10);

        // Act
        log.onUpdate();

        // Assert
        assertNotEquals(initialUpdate, log.getUpdatedAt(), "UpdatedAt harus berubah setelah update");
        assertTrue(log.getUpdatedAt().isAfter(initialUpdate), "UpdatedAt baru harus lebih besar dari yang lama");
        
        // CreatedAt tidak boleh berubah saat update
        LocalDateTime createdAt = log.getCreatedAt();
        log.onUpdate();
        assertEquals(createdAt, log.getCreatedAt(), "CreatedAt tidak boleh berubah saat onUpdate");
    }
}