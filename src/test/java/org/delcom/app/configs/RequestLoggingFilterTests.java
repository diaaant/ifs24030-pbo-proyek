package org.delcom.app.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLoggingFilterTests {

    private RequestLoggingFilter loggingFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    // Untuk menangkap output System.out
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        loggingFilter = new RequestLoggingFilter();
        // Alihkan System.out ke variable outContent
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        // Kembalikan System.out ke asalnya setelah test selesai
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Test Log Request Sukses (Status 200 - Green)")
    void testDoFilterInternalSuccess() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200); // 200 = Hijau

        // Act
        // Memanggil method protected doFilterInternal (bisa diakses karena package sama)
        loggingFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // 1. Pastikan chain dilanjutkan
        verify(filterChain, times(1)).doFilter(request, response);

        // 2. Cek output console
        String output = outContent.toString();
        
        // Verifikasi elemen penting dalam log
        assertTrue(output.contains("GET"), "Log harus mengandung method GET");
        assertTrue(output.contains("/api/users"), "Log harus mengandung URI");
        assertTrue(output.contains("200"), "Log harus mengandung status code");
        assertTrue(output.contains("\u001B[32m"), "Log harus berwarna HIJAU untuk status 200");
    }

    @Test
    @DisplayName("Test Log Request Error Server (Status 500 - Red)")
    void testDoFilterInternalServerError() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/login");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(response.getStatus()).thenReturn(500); // 500 = Merah

        // Act
        loggingFilter.doFilterInternal(request, response, filterChain);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("POST"));
        assertTrue(output.contains("500"));
        assertTrue(output.contains("\u001B[31m"), "Log harus berwarna MERAH untuk status 500");
    }

    @Test
    @DisplayName("Test Log Client Error (Status 400 - Yellow)")
    void testDoFilterInternalClientError() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/api/data");
        when(response.getStatus()).thenReturn(404); // 400s = Kuning

        // Act
        loggingFilter.doFilterInternal(request, response, filterChain);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("404"));
        assertTrue(output.contains("\u001B[33m"), "Log harus berwarna KUNING untuk status 400-an");
    }

    @Test
    @DisplayName("Test Skip Log untuk URI .well-known")
    void testDoFilterSkipWellKnown() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/.well-known/assetlinks.json");
        
        // Act
        loggingFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        
        // Log harus kosong karena di-skip
        assertEquals("", outContent.toString(), "Tidak boleh ada log untuk URI yang diawali /.well-known");
    }
}