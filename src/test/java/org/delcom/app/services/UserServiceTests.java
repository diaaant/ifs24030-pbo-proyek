package org.delcom.app.services;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Create User: Berhasil menyimpan user baru")
    void testCreateUser() {
        // Arrange
        String name = "Budi";
        String email = "budi@test.com";
        String password = "pass";

        // Mock repository.save untuk mengembalikan user yang sama saat dipanggil
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User createdUser = userService.createUser(name, email, password);

        // Assert
        assertNotNull(createdUser);
        assertEquals(name, createdUser.getName());
        assertEquals(email, createdUser.getEmail());
        assertEquals(password, createdUser.getPassword());
        
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Get User By Email: Ditemukan")
    void testGetUserByEmailFound() {
        // Arrange
        String email = "ada@test.com";
        User user = new User("Ada", email, "pass");
        
        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    @DisplayName("Get User By Email: Tidak Ditemukan (Return Null)")
    void testGetUserByEmailNotFound() {
        // Arrange
        String email = "gaib@test.com";
        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.empty());

        // Act
        User result = userService.getUserByEmail(email);

        // Assert
        assertNull(result, "Harus return null jika user tidak ada");
    }

    @Test
    @DisplayName("Get User By ID: Ditemukan")
    void testGetUserByIdFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    @DisplayName("Update User: Berhasil update data")
    void testUpdateUserSuccess() {
        // Arrange
        UUID id = UUID.randomUUID();
        User existingUser = new User("Lama", "lama@test.com", "pass");
        existingUser.setId(id);

        // Data baru
        String newName = "Baru";
        String newEmail = "baru@test.com";

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        User updatedUser = userService.updateUser(id, newName, newEmail);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(newName, updatedUser.getName());   // Nama harus berubah
        assertEquals(newEmail, updatedUser.getEmail()); // Email harus berubah
        
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Update User: Gagal jika user tidak ada")
    void testUpdateUserNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        User result = userService.updateUser(id, "Nama", "email@test.com");

        // Assert
        assertNull(result);
        verify(userRepository, never()).save(any()); // Save tidak boleh dipanggil
    }

    @Test
    @DisplayName("Update Password: Berhasil")
    void testUpdatePasswordSuccess() {
        // Arrange
        UUID id = UUID.randomUUID();
        User existingUser = new User("User", "user@test.com", "oldPass");
        existingUser.setId(id);

        String newPassword = "newSecretPassword";

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        User result = userService.updatePassword(id, newPassword);

        // Assert
        assertNotNull(result);
        assertEquals(newPassword, result.getPassword()); // Password harus berubah
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Update Password: Gagal jika user tidak ada")
    void testUpdatePasswordNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        User result = userService.updatePassword(id, "newPass");

        // Assert
        assertNull(result);
        verify(userRepository, never()).save(any());
    }
}