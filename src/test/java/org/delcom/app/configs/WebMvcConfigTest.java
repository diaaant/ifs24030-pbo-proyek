package org.delcom.app.configs;

import org.delcom.app.interceptors.AuthInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebMvcConfigTests {

    // Karena pom.xml sudah diupdate, @Mock ini aman untuk Java 25
    @Mock
    private AuthInterceptor authInterceptor;

    @Mock
    private InterceptorRegistry registry;

    @Mock
    private InterceptorRegistration registration;

    @InjectMocks
    private WebMvcConfig webMvcConfig;

    @Test
    @DisplayName("Test Konfigurasi Interceptor (Path Patterns & Exclusions)")
    void testAddInterceptors() {
        // Arrange
        // Simulasi Method Chaining: .addInterceptor() -> .addPathPatterns() -> .excludePathPatterns()
        
        // 1. Saat addInterceptor dipanggil, kembalikan objek registration mock
        when(registry.addInterceptor(authInterceptor)).thenReturn(registration);
        
        // 2. Saat addPathPatterns dipanggil, kembalikan objek registration lagi
        when(registration.addPathPatterns(anyString())).thenReturn(registration);
        
        // 3. Saat excludePathPatterns dipanggil, kembalikan objek registration lagi
        when(registration.excludePathPatterns(anyString())).thenReturn(registration);

        // Act
        webMvcConfig.addInterceptors(registry);

        // Assert
        // Verifikasi urutan pemanggilan sesuai kode main
        
        // Cek 1: Pastikan interceptor didaftarkan
        verify(registry).addInterceptor(authInterceptor);

        // Cek 2: Pastikan pattern "/api/**" ditambahkan
        verify(registration).addPathPatterns("/api/**");

        // Cek 3: Pastikan pengecualian path auth dan public ditambahkan
        verify(registration).excludePathPatterns("/api/auth/**");
        verify(registration).excludePathPatterns("/api/public/**");
    }
}