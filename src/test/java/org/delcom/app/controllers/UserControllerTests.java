package org.delcom.app.controllers;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.AuthToken;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthTokenService;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserControllerTests {

    @Mock private UserService userService;
    @Mock private AuthTokenService authTokenService;
    @Mock private AuthContext authContext;

    @InjectMocks private UserController userController;

    private User mockUser;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        // Inject AuthContext karena menggunakan @Autowired field injection
        ReflectionTestUtils.setField(userController, "authContext", authContext);

        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        // Set password terenkripsi agar validasi matches() berhasil
        mockUser.setPassword(encoder.encode("password123")); 
    }

    // ========================================================================
    // 1. REGISTER TESTS (Validation & Success)
    // ========================================================================
    @Test
    void testRegister_InvalidData() {
        User u = new User();
        // 1. Name Null
        assertEquals(400, userController.registerUser(u).getStatusCode().value());
        
        // 2. Name Empty
        u.setName("");
        assertEquals(400, userController.registerUser(u).getStatusCode().value());

        // 3. Email Null
        u.setName("Valid Name");
        assertEquals(400, userController.registerUser(u).getStatusCode().value());

        // 4. Email Empty
        u.setEmail("");
        assertEquals(400, userController.registerUser(u).getStatusCode().value());

        // 5. Password Null
        u.setEmail("valid@email.com");
        assertEquals(400, userController.registerUser(u).getStatusCode().value());

        // 6. Password Empty
        u.setPassword("");
        assertEquals(400, userController.registerUser(u).getStatusCode().value());
    }

    @Test
    void testRegister_EmailExists() {
        User u = new User("Name", "exist@test.com", "pass");
        when(userService.getUserByEmail(u.getEmail())).thenReturn(new User());
        assertEquals(400, userController.registerUser(u).getStatusCode().value());
    }

    @Test
    void testRegister_Success() {
        User u = new User("Name", "new@test.com", "pass");
        when(userService.getUserByEmail(u.getEmail())).thenReturn(null);
        when(userService.createUser(any(), any(), any())).thenReturn(mockUser);
        
        assertEquals(200, userController.registerUser(u).getStatusCode().value());
    }

    // ========================================================================
    // 2. LOGIN TESTS
    // ========================================================================
    @Test
    void testLogin_InvalidData() {
        User u = new User();
        // Email Null
        assertEquals(400, userController.loginUser(u).getStatusCode().value());
        
        // Email Empty
        u.setEmail("");
        assertEquals(400, userController.loginUser(u).getStatusCode().value());

        // Password Null
        u.setEmail("test@test.com");
        assertEquals(400, userController.loginUser(u).getStatusCode().value());

        // Password Empty
        u.setPassword("");
        assertEquals(400, userController.loginUser(u).getStatusCode().value());
    }

    @Test
    void testLogin_UserNotFound() {
        User u = new User(null, "ghost@test.com", "pass");
        when(userService.getUserByEmail(u.getEmail())).thenReturn(null);
        assertEquals(400, userController.loginUser(u).getStatusCode().value());
    }

    @Test
    void testLogin_WrongPassword() {
        User u = new User(null, "test@example.com", "wrongpass");
        when(userService.getUserByEmail(u.getEmail())).thenReturn(mockUser);
        assertEquals(400, userController.loginUser(u).getStatusCode().value());
    }

    @Test
    void testLogin_Success() {
        User u = new User(null, "test@example.com", "password123");
        when(userService.getUserByEmail(u.getEmail())).thenReturn(mockUser);
        
        // Mocking Static Method JWT
        try (MockedStatic<JwtUtil> jwtMock = Mockito.mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.generateToken(any())).thenReturn("jwt-token");
            
            // Mock DB interactions
            when(authTokenService.findUserToken(any(), anyString())).thenReturn(new AuthToken());
            when(authTokenService.createAuthToken(any())).thenReturn(new AuthToken());

            assertEquals(200, userController.loginUser(u).getStatusCode().value());
            verify(authTokenService).deleteAuthToken(any());
        }
    }

    @Test
    void testLogin_TokenCreationFails() {
        User u = new User(null, "test@example.com", "password123");
        when(userService.getUserByEmail(u.getEmail())).thenReturn(mockUser);

        try (MockedStatic<JwtUtil> jwtMock = Mockito.mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.generateToken(any())).thenReturn("jwt-token");
            // Simulate Save Failed (Return Null)
            when(authTokenService.createAuthToken(any())).thenReturn(null);

            assertEquals(500, userController.loginUser(u).getStatusCode().value());
        }
    }

    // ========================================================================
    // 3. GET USER INFO TESTS (Menutup Branch !isAuthenticated)
    // ========================================================================
    @Test
    void testGetUserInfo_Unauthorized() {
        when(authContext.isAuthenticated()).thenReturn(false); // CASE 401
        assertEquals(401, userController.getUserInfo().getStatusCode().value());
    }

    @Test
    void testGetUserInfo_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        
        assertEquals(200, userController.getUserInfo().getStatusCode().value());
    }

    // ========================================================================
    // 4. UPDATE USER TESTS (Menutup Branch Validasi & Unauthorized)
    // ========================================================================
    @Test
    void testUpdateUser_Unauthorized() {
        when(authContext.isAuthenticated()).thenReturn(false); // CASE 401
        assertEquals(401, userController.updateUser(new User()).getStatusCode().value());
    }

    @Test
    void testUpdateUser_Validation() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);

        User req = new User();
        // 1. Name Null
        assertEquals(400, userController.updateUser(req).getStatusCode().value());
        
        // 2. Name Empty
        req.setName("");
        assertEquals(400, userController.updateUser(req).getStatusCode().value());

        // 3. Email Null
        req.setName("New Name");
        assertEquals(400, userController.updateUser(req).getStatusCode().value());

        // 4. Email Empty
        req.setEmail("");
        assertEquals(400, userController.updateUser(req).getStatusCode().value());
    }

    @Test
    void testUpdateUser_SuccessAndNotFound() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        User req = new User("New", "new@mail.com", null);

        // Case: User Not Found (Service returns null)
        when(userService.updateUser(any(), anyString(), anyString())).thenReturn(null);
        assertEquals(404, userController.updateUser(req).getStatusCode().value());

        // Case: Success
        when(userService.updateUser(any(), anyString(), anyString())).thenReturn(mockUser);
        assertEquals(200, userController.updateUser(req).getStatusCode().value());
    }

    // ========================================================================
    // 5. UPDATE PASSWORD TESTS (Menutup Branch Null/Empty secara detail)
    // ========================================================================
    @Test
    void testUpdateUserPassword_Unauthorized() {
        when(authContext.isAuthenticated()).thenReturn(false); // CASE 401
        assertEquals(401, userController.updateUserPassword(new HashMap<>()).getStatusCode().value());
    }

    @Test
    void testUpdateUserPassword_InputValidation() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);

        Map<String, String> payload = new HashMap<>();
        
        // 1. Old Password NULL
        assertEquals(400, userController.updateUserPassword(payload).getStatusCode().value());

        // 2. Old Password EMPTY
        payload.put("password", "");
        payload.put("newPassword", "new");
        assertEquals(400, userController.updateUserPassword(payload).getStatusCode().value());

        // 3. New Password NULL
        payload.clear();
        payload.put("password", "old");
        assertEquals(400, userController.updateUserPassword(payload).getStatusCode().value());

        // 4. New Password EMPTY
        payload.put("newPassword", "");
        assertEquals(400, userController.updateUserPassword(payload).getStatusCode().value());
    }

    @Test
    void testUpdateUserPassword_WrongOldPassword() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);

        Map<String, String> payload = new HashMap<>();
        payload.put("password", "wrongpass"); // Password salah
        payload.put("newPassword", "newpass");

        assertEquals(400, userController.updateUserPassword(payload).getStatusCode().value());
    }

    @Test
    void testUpdateUserPassword_UserNotFound() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);

        Map<String, String> payload = new HashMap<>();
        payload.put("password", "password123"); // Password benar
        payload.put("newPassword", "newpass");

        // Service return null (User hilang dari DB)
        when(userService.updatePassword(any(), anyString())).thenReturn(null);
        assertEquals(404, userController.updateUserPassword(payload).getStatusCode().value());
    }

    @Test
    void testUpdateUserPassword_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);

        Map<String, String> payload = new HashMap<>();
        payload.put("password", "password123");
        payload.put("newPassword", "newpass");

        // Success Update
        when(userService.updatePassword(any(), anyString())).thenReturn(mockUser);
        
        ResponseEntity<org.delcom.app.configs.ApiResponse<Void>> response = userController.updateUserPassword(payload);
        assertEquals(200, response.getStatusCode().value());
        
        // Verify Token Logout
        verify(authTokenService).deleteAuthToken(mockUser.getId());
    }
}