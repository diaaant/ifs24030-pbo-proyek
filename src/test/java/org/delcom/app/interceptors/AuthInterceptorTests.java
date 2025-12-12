package org.delcom.app.interceptors;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.AuthToken;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthTokenService;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthInterceptorTests {

    @Mock private AuthContext authContext;
    @Mock private AuthTokenService authTokenService;
    @Mock private UserService userService;
    @InjectMocks private AuthInterceptor authInterceptor;

    private MockedStatic<JwtUtil> jwtUtilMock;

    @BeforeEach
    void setUp() {
        jwtUtilMock = Mockito.mockStatic(JwtUtil.class);
    }

    @AfterEach
    void tearDown() {
        jwtUtilMock.close();
    }

    // 1. Endpoint Public
    @Test
    void testPreHandle_PublicEndpoint() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/login");
        assertTrue(authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    // 2. Endpoint Error
    @Test
    void testPreHandle_ErrorEndpoint() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/error");
        assertTrue(authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    // 3. Token Missing (NULL) -> Menutup cabang "token == null"
    @Test
    void testPreHandle_TokenMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/protected");
        // Tidak ada header Authorization
        assertFalse(authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    // 4. Token Format Salah (Basic) -> Menutup cabang "token == null" (karena extractToken return null)
    @Test
    void testPreHandle_TokenInvalidFormat() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/protected");
        request.addHeader("Authorization", "Basic 123");
        assertFalse(authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    // 5. Token Empty String ("") -> PENTING! Menutup cabang "token.isEmpty()"
    @Test
    void testPreHandle_TokenEmptyString() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/protected");
        
        // Header "Bearer " (dengan spasi di akhir). 
        // extractToken akan melakukan substring(7) yang menghasilkan string kosong ""
        request.addHeader("Authorization", "Bearer "); 
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean result = authInterceptor.preHandle(request, response, new Object());

        assertFalse(result);
        // Pastikan error messagenya benar
        assertTrue(response.getContentAsString().contains("Token autentikasi tidak ditemukan"));
    }

    // 6. JWT Invalid Signature
    @Test
    void testPreHandle_JwtInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/protected");
        request.addHeader("Authorization", "Bearer invalid-jwt");
        
        jwtUtilMock.when(() -> JwtUtil.validateToken(anyString(), eq(true))).thenReturn(false);
        assertFalse(authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    // 7. Extract UserID Null
    @Test
    void testPreHandle_ExtractIdNull() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/protected");
        request.addHeader("Authorization", "Bearer token");

        jwtUtilMock.when(() -> JwtUtil.validateToken(anyString(), eq(true))).thenReturn(true);
        jwtUtilMock.when(() -> JwtUtil.extractUserId(anyString())).thenReturn(null);

        assertFalse(authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    // 8. Token Not Found in DB
    @Test
    void testPreHandle_TokenNotInDB() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/protected");
        request.addHeader("Authorization", "Bearer token");
        UUID uid = UUID.randomUUID();

        jwtUtilMock.when(() -> JwtUtil.validateToken(anyString(), eq(true))).thenReturn(true);
        jwtUtilMock.when(() -> JwtUtil.extractUserId(anyString())).thenReturn(uid);
        when(authTokenService.findUserToken(uid, "token")).thenReturn(null);

        assertFalse(authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    // 9. User Not Found
    @Test
    void testPreHandle_UserNotFound() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/protected");
        request.addHeader("Authorization", "Bearer token");
        UUID uid = UUID.randomUUID();
        AuthToken at = new AuthToken(uid, "token");

        jwtUtilMock.when(() -> JwtUtil.validateToken(anyString(), eq(true))).thenReturn(true);
        jwtUtilMock.when(() -> JwtUtil.extractUserId(anyString())).thenReturn(uid);
        when(authTokenService.findUserToken(uid, "token")).thenReturn(at);
        when(userService.getUserById(uid)).thenReturn(null);

        assertFalse(authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    // 10. Success (Happy Path) -> Menutup cabang Else (Tidak masuk if manapun)
    @Test
    void testPreHandle_Success() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/protected");
        request.addHeader("Authorization", "Bearer token");
        UUID uid = UUID.randomUUID();
        AuthToken at = new AuthToken(uid, "token");
        User u = new User(); u.setId(uid);

        jwtUtilMock.when(() -> JwtUtil.validateToken(anyString(), eq(true))).thenReturn(true);
        jwtUtilMock.when(() -> JwtUtil.extractUserId(anyString())).thenReturn(uid);
        when(authTokenService.findUserToken(uid, "token")).thenReturn(at);
        when(userService.getUserById(uid)).thenReturn(u);

        assertTrue(authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
        verify(authContext).setAuthUser(u);
    }
}