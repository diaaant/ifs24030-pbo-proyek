package org.delcom.app.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginFormTests {

    private static Validator validator;

    // Inisialisasi Validator Engine sekali saja sebelum semua test dijalankan
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test Setters dan Getters (Data Roundtrip)")
    void testSettersAndGetters() {
        // Arrange
        LoginForm loginForm = new LoginForm();
        String email = "test@domain.com";
        String password = "rahasia123";
        boolean remember = true;

        // Act
        loginForm.setEmail(email);
        loginForm.setPassword(password);
        loginForm.setRememberMe(remember);

        // Assert
        assertEquals(email, loginForm.getEmail());
        assertEquals(password, loginForm.getPassword());
        assertTrue(loginForm.isRememberMe()); // getter boolean biasanya is...
    }

    @Test
    @DisplayName("Test Validasi: Input Valid")
    void testValidationSuccess() {
        // Arrange
        LoginForm loginForm = new LoginForm();
        loginForm.setEmail("user@example.com");
        loginForm.setPassword("password123");
        loginForm.setRememberMe(false);

        // Act
        Set<ConstraintViolation<LoginForm>> violations = validator.validate(loginForm);

        // Assert
        assertTrue(violations.isEmpty(), "Seharusnya tidak ada error validasi untuk data yang benar");
    }

    @Test
    @DisplayName("Test Validasi: Format Email Salah")
    void testValidationInvalidEmail() {
        // Arrange
        LoginForm loginForm = new LoginForm();
        loginForm.setEmail("bukan-email"); // Format salah
        loginForm.setPassword("password123");

        // Act
        Set<ConstraintViolation<LoginForm>> violations = validator.validate(loginForm);

        // Assert
        assertFalse(violations.isEmpty(), "Harus ada error validasi");
        
        // Cek apakah error message sesuai dengan anotasi di kode Anda
        boolean messageFound = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Format email tidak valid"));
        
        assertTrue(messageFound, "Pesan error 'Format email tidak valid' harus muncul");
    }

    @Test
    @DisplayName("Test Validasi: Field Kosong")
    void testValidationBlankFields() {
        // Arrange
        LoginForm loginForm = new LoginForm();
        loginForm.setEmail(""); // Kosong
        loginForm.setPassword(null); // Null

        // Act
        Set<ConstraintViolation<LoginForm>> violations = validator.validate(loginForm);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size(), "Harus ada 2 error (email kosong & password kosong)");
        
        // Cek pesan error
        boolean emailError = violations.stream().anyMatch(v -> v.getMessage().equals("Email harus diisi"));
        boolean passError = violations.stream().anyMatch(v -> v.getMessage().equals("Kata sandi harus diisi"));

        assertTrue(emailError);
        assertTrue(passError);
    }
}