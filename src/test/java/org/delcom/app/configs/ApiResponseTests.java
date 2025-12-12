package org.delcom.app.configs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTests {

    @Test
    @DisplayName("Test Constructor dan Getters dengan tipe String")
    void testConstructorAndGettersString() {
        // Arrange
        String expectedStatus = "success";
        String expectedMessage = "Data loaded";
        String expectedData = "Sample Data";

        // Act
        ApiResponse<String> response = new ApiResponse<>(expectedStatus, expectedMessage, expectedData);

        // Assert
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedMessage, response.getMessage());
        assertEquals(expectedData, response.getData());
    }

    @Test
    @DisplayName("Test Constructor dan Getters dengan tipe Integer")
    void testConstructorAndGettersInteger() {
        // Arrange
        String status = "ok";
        String message = "Count retrieved";
        Integer dataValue = 100;

        // Act
        ApiResponse<Integer> response = new ApiResponse<>(status, message, dataValue);

        // Assert
        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(dataValue, response.getData());
    }

    @Test
    @DisplayName("Test dengan Data Null")
    void testConstructorWithNullData() {
        // Arrange
        String status = "error";
        String message = "Not found";

        // Act
        ApiResponse<Object> response = new ApiResponse<>(status, message, null);

        // Assert
        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertNull(response.getData(), "Data harus null sesuai input konstruktor");
    }
}