package org.delcom.app.configs;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupInfoLogger implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();

        // 1. Ambil Port (Default 8080)
        String port = env.getProperty("server.port", "8080");

        // 2. Ambil Context Path & Normalisasi (Agar tidak null)
        String contextPath = env.getProperty("server.servlet.context-path");
        if (contextPath == null || contextPath.isBlank() || contextPath.equals("/")) {
            contextPath = "";
        }

        // 3. Tentukan HTTP/HTTPS (Cek keberadaan SSL Key Store)
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }

        // 4. Warna Terminal (Agar output cantik)
        String GREEN = "\u001B[32m";
        String CYAN = "\u001B[36m";
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";

        // 5. Cetak Output ke Terminal
        System.out.println("\n" + "----------------------------------------------------------");
        System.out.println(GREEN + BOLD + "   Aplikasi Berhasil Dijalankan! 🚀" + RESET);
        System.out.println("----------------------------------------------------------");
        // Baris ini yang dicek oleh Unit Test
        System.out.println("   👉 Link:   " + CYAN + protocol + "://localhost:" + port + contextPath + RESET);
        System.out.println("----------------------------------------------------------\n");
    }
}