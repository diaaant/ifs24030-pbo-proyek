package org.delcom.app.views;

import org.delcom.app.dto.TravelLogForm;
import org.delcom.app.entities.TravelLog;
import org.delcom.app.entities.User;
import org.delcom.app.services.FileStorageService;
import org.delcom.app.services.TravelLogService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;

import java.nio.file.Path;
import java.util.UUID;
import java.util.List; // Wajib ada untuk List<TravelLog>
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class TravelLogController {

    private final TravelLogService service;
    private final FileStorageService fileService;

    public TravelLogController(TravelLogService service, FileStorageService fileService) {
        this.service = service;
        this.fileService = fileService;
    }

    private User getAuthUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) return (User) principal;
        return null;
    }

    // --- HALAMAN UTAMA (HOME) ---
    @GetMapping("/")
    public String home(Model model, @RequestParam(required = false) String search) {
        User user = getAuthUser();
        if (user == null) return "redirect:/auth/login";

        // 1. Ambil Data Logs ke dalam variabel List
        List<TravelLog> logs = service.getAll(user.getId(), search);

        // 2. Kirim data ke HTML
        model.addAttribute("auth", user);
        model.addAttribute("logs", logs);
        model.addAttribute("logForm", new TravelLogForm());
        
        // --- LOGIKA HITUNG TOTAL BIAYA (GRAND TOTAL) ---
        double grandTotal = logs.stream()
                                .mapToDouble(TravelLog::getTotalCost)
                                .sum();
        model.addAttribute("grandTotal", grandTotal);
        // -----------------------------------------------

        // Data untuk Chart (Total Biaya per Destinasi)
        Map<String, Double> chartData = service.getAll(user.getId(), "").stream()
            .collect(Collectors.groupingBy(TravelLog::getDestination, 
                     Collectors.summingDouble(TravelLog::getTotalCost)));
        model.addAttribute("chartDataLabels", chartData.keySet());
        model.addAttribute("chartDataValues", chartData.values());

        return "pages/home";
    }

    // --- TAMBAH DATA ---
    @PostMapping("/logs/add")
    public String add(@ModelAttribute TravelLogForm form, RedirectAttributes ra) {
        User user = getAuthUser();
        try {
            TravelLog log = new TravelLog(user.getId(), form.getTitle(), form.getDestination(), 
                                          form.getDescription(), form.getTotalCost(), form.getRating());
            
            // Simpan Gambar jika ada
            if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
                String filename = fileService.storeFile(form.getImageFile(), UUID.randomUUID());
                log.setImagePath(filename);
            } else {
                log.setImagePath("default.jpg"); // Gambar placeholder
            }

            service.save(log);
            ra.addFlashAttribute("success", "Jurnal berhasil disimpan!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Gagal menyimpan: " + e.getMessage());
        }
        return "redirect:/";
    }

    // --- DETAIL DATA ---
    @GetMapping("/logs/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        User user = getAuthUser();
        TravelLog log = service.getById(user.getId(), id);
        if (log == null) return "redirect:/";
        
        model.addAttribute("log", log);
        model.addAttribute("auth", user);
        
        TravelLogForm form = new TravelLogForm();
        form.setId(log.getId());
        model.addAttribute("editForm", form); // Untuk modal edit gambar
        
        return "pages/detail";
    }

    // --- UPDATE GAMBAR ---
    @PostMapping("/logs/image")
    public String updateImage(@ModelAttribute TravelLogForm form, RedirectAttributes ra) {
        User user = getAuthUser();
        try {
            TravelLog log = service.getById(user.getId(), form.getId());
            if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
                String filename = fileService.storeFile(form.getImageFile(), log.getId());
                log.setImagePath(filename);
                service.save(log);
                ra.addFlashAttribute("success", "Foto berhasil diperbarui!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Gagal upload: " + e.getMessage());
        }
        return "redirect:/logs/" + form.getId();
    }
    
    // --- HAPUS DATA ---
    @GetMapping("/logs/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        User user = getAuthUser();
        service.delete(user.getId(), id);
        ra.addFlashAttribute("success", "Jurnal berhasil dihapus.");
        return "redirect:/";
    }

    // --- SERVE GAMBAR ---
    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = fileService.loadFile(filename);
            Resource resource = new UrlResource(file.toUri());
            return ResponseEntity.ok().body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}