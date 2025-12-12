package org.delcom.app.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TravelLogFormTests {

    @Test
    @DisplayName("Test Setters dan Getters untuk semua field")
    void testSettersAndGetters() {
        // Arrange
        TravelLogForm form = new TravelLogForm();
        
        // Data Dummy
        UUID id = UUID.randomUUID();
        String title = "Liburan Musim Panas";
        String destination = "Raja Ampat";
        String description = "Pemandangan bawah laut yang indah";
        Double totalCost = 15000000.0;
        Integer rating = 5;
        
        // Mock MultipartFile (karena interface)
        MultipartFile mockFile = mock(MultipartFile.class);

        // Act (Set nilai)
        form.setId(id);
        form.setTitle(title);
        form.setDestination(destination);
        form.setDescription(description);
        form.setTotalCost(totalCost);
        form.setRating(rating);
        form.setImageFile(mockFile);

        // Assert (Verifikasi nilai)
        assertEquals(id, form.getId());
        assertEquals(title, form.getTitle());
        assertEquals(destination, form.getDestination());
        assertEquals(description, form.getDescription());
        assertEquals(totalCost, form.getTotalCost());
        assertEquals(rating, form.getRating());
        assertEquals(mockFile, form.getImageFile());
    }

    @Test
    @DisplayName("Test Handling Null Values")
    void testNullValues() {
        // Arrange
        TravelLogForm form = new TravelLogForm();

        // Act
        // Set semua ke null (untuk memastikan tipe data Wrapper Class digunakan dengan benar)
        form.setId(null);
        form.setTitle(null);
        form.setDestination(null);
        form.setDescription(null);
        form.setTotalCost(null);
        form.setRating(null);
        form.setImageFile(null);

        // Assert
        assertNull(form.getId());
        assertNull(form.getTitle());
        assertNull(form.getDestination());
        assertNull(form.getDescription());
        assertNull(form.getTotalCost());
        assertNull(form.getRating());
        assertNull(form.getImageFile());
    }
}