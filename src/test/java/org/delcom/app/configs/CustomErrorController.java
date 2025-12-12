package org.delcom.app.configs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTests {

    @Mock
    private ErrorAttributes errorAttributes;

    @Mock
    private HttpServletRequest request;

    private CustomErrorController customErrorController;

    @BeforeEach
    void setUp() {
        // Inisialisasi controller dengan mock ErrorAttributes
        customErrorController = new CustomErrorController(errorAttributes);
    }

    @Test
    @DisplayName("Test Error 500 (Internal Server Error) -> status 'error'")
    void testHandleError500() {
        // Arrange
        Map<String, Object> mockAttributes = new HashMap<>();
        mockAttributes.put("status", 500);
        mockAttributes.put("error", "Internal Server Error");
        mockAttributes.put("path", "/api/test");

        // Ketika errorAttributes.getErrorAttributes dipanggil, kembalikan map buatan kita
        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
                .thenReturn(mockAttributes);

        // Act
        ResponseEntity<Map<String, Object>> response = customErrorController.handleError(request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("error", body.get("status")); // Logika: status == 500 ? "error" : "fail"
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("Endpoint tidak ditemukan atau terjadi error", body.get("message"));
        assertEquals("/api/test", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Test Error 404 (Not Found) -> status 'fail'")
    void testHandleError404() {
        // Arrange
        Map<String, Object> mockAttributes = new HashMap<>();
        mockAttributes.put("status", 404);
        mockAttributes.put("error", "Not Found");
        mockAttributes.put("path", "/api/unknown");

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
                .thenReturn(mockAttributes);

        // Act
        ResponseEntity<Map<String, Object>> response = customErrorController.handleError(request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("fail", body.get("status")); // Logika: status != 500 -> "fail"
        assertEquals("/api/unknown", body.get("path"));
    }

    @Test
    @DisplayName("Test Default Values (Ketika attributes kosong)")
    void testHandleErrorDefaults() {
        // Arrange
        Map<String, Object> emptyAttributes = new HashMap<>();
        // Tidak mengisi status, path, dll. Simulasi jika ErrorAttributes gagal mengambil data.

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
                .thenReturn(emptyAttributes);

        // Act
        ResponseEntity<Map<String, Object>> response = customErrorController.handleError(request);

        // Assert
        // Kode Anda: attributes.getOrDefault("status", 500);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("error", body.get("status")); // Default 500 -> "error"
        assertEquals("unknown", body.get("path")); // Default path -> "unknown"
        assertEquals("Unknown Error", body.get("error")); // Default error -> "Unknown Error"
    }
}