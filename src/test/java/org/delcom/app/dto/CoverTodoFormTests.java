package org.delcom.app.dto;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CoverTodoFormTest {

    // --- 1. Test Getter & Setter & Constructor ---
    @Test
    void testGettersAndSetters() {
        CoverTodoForm form = new CoverTodoForm();
        UUID id = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());

        form.setId(id);
        form.setCoverFile(file);

        assertEquals(id, form.getId());
        assertEquals(file, form.getCoverFile());
    }

    // --- 2. Test isEmpty() ---
    @Test
    void testIsEmpty() {
        CoverTodoForm form = new CoverTodoForm();
        
        // Case 1: File Null -> Empty
        form.setCoverFile(null);
        assertTrue(form.isEmpty());

        // Case 2: File Kosong (0 bytes) -> Empty
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
        form.setCoverFile(emptyFile);
        assertTrue(form.isEmpty());

        // Case 3: File Ada Isi -> Not Empty
        MockMultipartFile validFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        form.setCoverFile(validFile);
        assertFalse(form.isEmpty());
    }

    // --- 3. Test getOriginalFilename() ---
    @Test
    void testGetOriginalFilename() {
        CoverTodoForm form = new CoverTodoForm();

        // Case 1: File Null
        form.setCoverFile(null);
        assertNull(form.getOriginalFilename());

        // Case 2: File Ada
        MockMultipartFile file = new MockMultipartFile("file", "liburan.jpg", "image/jpeg", "content".getBytes());
        form.setCoverFile(file);
        assertEquals("liburan.jpg", form.getOriginalFilename());
    }

    // --- 4. Test isValidImage() ---
    @Test
    void testIsValidImage() {
        CoverTodoForm form = new CoverTodoForm();

        // Case 1: Empty/Null File -> False
        form.setCoverFile(null);
        assertFalse(form.isValidImage());

        // Case 2: Valid Image Types -> True
        String[] validTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};
        for (String type : validTypes) {
            MockMultipartFile file = new MockMultipartFile("file", "test", type, "content".getBytes());
            form.setCoverFile(file);
            assertTrue(form.isValidImage(), "Should be valid for type: " + type);
        }

        // Case 3: Invalid Type (e.g. PDF) -> False
        MockMultipartFile pdfFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "content".getBytes());
        form.setCoverFile(pdfFile);
        assertFalse(form.isValidImage());
        
        // Case 4: Content Type Null -> False (Edge Case)
        MockMultipartFile nullTypeFile = new MockMultipartFile("file", "test", null, "content".getBytes());
        form.setCoverFile(nullTypeFile);
        assertFalse(form.isValidImage());
    }

    // --- 5. Test isSizeValid() ---
    @Test
    void testIsSizeValid() {
        CoverTodoForm form = new CoverTodoForm();
        
        // File ukuran 10 bytes
        byte[] content = "1234567890".getBytes(); 
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);
        form.setCoverFile(file);

        // Case 1: File Null -> False
        form.setCoverFile(null);
        assertFalse(form.isSizeValid(100));

        // Case 2: File Size <= Max Size -> True
        form.setCoverFile(file);
        assertTrue(form.isSizeValid(10)); // Pas
        assertTrue(form.isSizeValid(20)); // Kurang dari limit

        // Case 3: File Size > Max Size -> False
        assertFalse(form.isSizeValid(5)); // Lebih besar dari limit
    }
}