package org.delcom.app.configs;

import org.delcom.app.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthContextTests {

    @Test
    @DisplayName("Test default state (Not Authenticated)")
    void testDefaultState() {
        AuthContext authContext = new AuthContext();
        assertNull(authContext.getAuthUser());
        assertFalse(authContext.isAuthenticated());
    }

    @Test
    @DisplayName("Test setAuthUser dan isAuthenticated (Authenticated)")
    void testSetUserAndAuthentication() {
        AuthContext authContext = new AuthContext();
        
        // REVISI: Pakai objek asli, jangan di-mock
        User realUser = new User();
        realUser.setName("Test User");

        authContext.setAuthUser(realUser);

        assertEquals(realUser, authContext.getAuthUser());
        assertTrue(authContext.isAuthenticated());
    }

    @Test
    @DisplayName("Test setAuthUser kembali ke null (Logout scenario)")
    void testSetUserToNull() {
        AuthContext authContext = new AuthContext();
        authContext.setAuthUser(new User()); // Pakai objek asli
        
        assertTrue(authContext.isAuthenticated());

        authContext.setAuthUser(null);

        assertNull(authContext.getAuthUser());
        assertFalse(authContext.isAuthenticated());
    }
}