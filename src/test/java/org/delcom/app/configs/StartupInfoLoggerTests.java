package org.delcom.app.configs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StartupInfoLoggerTests {

    @InjectMocks
    private StartupInfoLogger startupInfoLogger;

    @Mock
    private ApplicationReadyEvent event;

    @Mock
    private ConfigurableApplicationContext context;

    @Mock
    private ConfigurableEnvironment env;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));

        // Wiring dasar: Event -> Context -> Environment
        doReturn(context).when(event).getApplicationContext();
        doReturn(env).when(context).getEnvironment();

        // Default Stubbing (Agar tidak NullPointer)
        doReturn("8080").when(env).getProperty("server.port", "8080");
        doReturn(null).when(env).getProperty("server.servlet.context-path");
        doReturn(null).when(env).getProperty("server.ssl.key-store");
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Test Default Startup (localhost:8080)")
    void testDefaultStartup() {
        // Arrange (Pakai default stubbing dari setUp)

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outContent.toString();
        // Cek potongan string penting
        assertTrue(output.contains("http://localhost:8080"));
        assertTrue(output.contains("Aplikasi Berhasil Dijalankan"));
    }

    @Test
    @DisplayName("Test Custom Configuration (localhost:9090/api)")
    void testCustomConfiguration() {
        // Arrange
        // Override return value untuk test case ini
        doReturn("9090").when(env).getProperty("server.port", "8080");
        doReturn("/api").when(env).getProperty("server.servlet.context-path");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("http://localhost:9090/api"));
    }

    @Test
    @DisplayName("Test HTTPS Protocol")
    void testHttpsProtocol() {
        // Arrange
        // Jika server.ssl.key-store tidak null, harusnya jadi https
        doReturn("my-keystore.p12").when(env).getProperty("server.ssl.key-store");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("https://localhost:8080"));
    }
}