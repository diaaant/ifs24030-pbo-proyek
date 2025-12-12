package org.delcom.app.configs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTests {

    @Test
    @DisplayName("Test PasswordEncoder Bean dan Logika Hashing")
    void testPasswordEncoder() {
        // Arrange
        SecurityConfig securityConfig = new SecurityConfig();

        // Act
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        // Assert 1: Pastikan tipe instance benar
        assertNotNull(encoder, "PasswordEncoder bean tidak boleh null");
        assertTrue(encoder instanceof BCryptPasswordEncoder, "PasswordEncoder harus menggunakan implementasi BCrypt");

        // Assert 2: Test logika Hashing (BCrypt)
        String rawPassword = "RahasiaNegara";
        String encodedPassword = encoder.encode(rawPassword);

        // Password yang di-encode tidak boleh sama dengan plain text
        assertNotEquals(rawPassword, encodedPassword, "Password tidak boleh disimpan dalam plain text");

        // Password yang di-encode harus bisa diverifikasi kembali (matches)
        assertTrue(encoder.matches(rawPassword, encodedPassword), "Encoder harus valid mencocokkan password asli dengan hash");
        
        // Password yang salah harus ditolak
        assertFalse(encoder.matches("SalahPassword", encodedPassword), "Encoder harus menolak password yang salah");
    }
}