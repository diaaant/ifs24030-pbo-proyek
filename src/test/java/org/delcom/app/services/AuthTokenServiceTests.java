package org.delcom.app.services;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.repositories.AuthTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTests {

    @Mock
    private AuthTokenRepository authTokenRepository;

    @InjectMocks
    private AuthTokenService authTokenService;

    @Test
    @DisplayName("Test Find User Token")
    void testFindUserToken() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String tokenStr = "sample-jwt-token";
        AuthToken expectedToken = new AuthToken(userId, tokenStr);

        // Simulasi repository mengembalikan token
        when(authTokenRepository.findUserToken(userId, tokenStr)).thenReturn(expectedToken);

        // Act
        AuthToken result = authTokenService.findUserToken(userId, tokenStr);

        // Assert
        assertEquals(expectedToken, result);
        // Pastikan method repository dipanggil tepat 1 kali
        verify(authTokenRepository, times(1)).findUserToken(userId, tokenStr);
    }

    @Test
    @DisplayName("Test Create Auth Token")
    void testCreateAuthToken() {
        // Arrange
        AuthToken newToken = new AuthToken(UUID.randomUUID(), "token-baru");
        
        // Simulasi repository.save
        when(authTokenRepository.save(newToken)).thenReturn(newToken);

        // Act
        AuthToken result = authTokenService.createAuthToken(newToken);

        // Assert
        assertEquals(newToken, result);
        verify(authTokenRepository, times(1)).save(newToken);
    }

    @Test
    @DisplayName("Test Delete Auth Token")
    void testDeleteAuthToken() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        // Method ini void, jadi kita hanya memanggilnya
        authTokenService.deleteAuthToken(userId);

        // Assert
        // Verifikasi bahwa repository.deleteByUserId dipanggil dengan userId yang benar
        verify(authTokenRepository, times(1)).deleteByUserId(userId);
    }
}