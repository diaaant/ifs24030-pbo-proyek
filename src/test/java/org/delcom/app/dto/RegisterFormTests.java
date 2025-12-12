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

class RegisterFormTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        // Inisialisasi Validator untuk menguji anotasi @NotBlank dan @Email
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test Setters dan Getters")
    void testSettersAndGetters() {
        // Arrange
        RegisterForm form = new RegisterForm();
        String name = "Agus Subagio";
        String email = "agus@example.com";
        String password = "securePassword123";

        // Act
        form.setName(name);
        form.setEmail(email);
        form.setPassword(password);

        // Assert
        assertEquals(name, form.getName());
        assertEquals(email, form.getEmail());
        assertEquals(password, form.getPassword());
    }

    @Test
    @DisplayName("Test Validasi: Input Valid (Sukses)")
    void testValidationSuccess() {
        // Arrange
        RegisterForm form = new RegisterForm();
        form.setName("User Valid");
        form.setEmail("valid@example.com");
        form.setPassword("123456");

        // Act
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        // Assert
        assertTrue(violations.isEmpty(), "Seharusnya tidak ada error validasi jika data benar");
    }

    @Test
    @DisplayName("Test Validasi: Field Kosong (Blank)")
    void testValidationBlankFields() {
        // Arrange
        RegisterForm form = new RegisterForm();
        form.setName("");     // Kosong
        form.setEmail(null);  // Null
        form.setPassword(""); // Kosong

        // Act
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        // Assert - Memastikan pesan error sesuai dengan kode main Anda
        
        // Cek @NotBlank(message = "Nama harus diisi")
        boolean nameError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Nama harus diisi"));

        // Cek @NotBlank(message = "Email harus diisi")
        boolean emailError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Email harus diisi"));

        // Cek @NotBlank(message = "Kata sandi harus diisi")
        boolean passError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Kata sandi harus diisi"));

        assertTrue(nameError, "Pesan error 'Nama harus diisi' tidak ditemukan");
        assertTrue(emailError, "Pesan error 'Email harus diisi' tidak ditemukan");
        assertTrue(passError, "Pesan error 'Kata sandi harus diisi' tidak ditemukan");
    }

    @Test
    @DisplayName("Test Validasi: Format Email Salah")
    void testValidationInvalidEmail() {
        // Arrange
        RegisterForm form = new RegisterForm();
        form.setName("Budi");
        form.setEmail("budi-bukan-email"); // Format salah
        form.setPassword("pass123");

        // Act
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        // Assert
        // Cek @Email(message = "Format email tidak valid")
        boolean emailFormatError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Format email tidak valid"));

        assertTrue(emailFormatError, "Pesan error 'Format email tidak valid' harus muncul");
    }
}