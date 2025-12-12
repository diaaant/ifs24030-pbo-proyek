package org.delcom.app.services;

import org.delcom.app.entities.TravelLog;
import org.delcom.app.repositories.TravelLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Tambahkan ini agar aman di Java 25
class TravelLogServiceTests {

    @Mock
    private TravelLogRepository repository;

    @InjectMocks
    private TravelLogService service;

    @Test
    @DisplayName("GetAll: Menggunakan Search jika keyword ada")
    void testGetAllWithKeyword() {
        UUID userId = UUID.randomUUID();
        String keyword = "Bali";
        List<TravelLog> expectedList = Collections.singletonList(new TravelLog());

        when(repository.search(userId, keyword)).thenReturn(expectedList);

        List<TravelLog> result = service.getAll(userId, keyword);

        assertEquals(expectedList, result);
        verify(repository).search(userId, keyword);
        verify(repository, never()).findByUserIdOrderByCreatedAtDesc(any());
    }

    @Test
    @DisplayName("GetAll: Menggunakan FindByUserId jika keyword null/blank")
    void testGetAllWithoutKeyword() {
        UUID userId = UUID.randomUUID();
        List<TravelLog> expectedList = Collections.singletonList(new TravelLog());

        when(repository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(expectedList);

        service.getAll(userId, null);
        service.getAll(userId, "   ");

        verify(repository, times(2)).findByUserIdOrderByCreatedAtDesc(userId);
        verify(repository, never()).search(any(), anyString());
    }

    @Test
    @DisplayName("GetById: User ditemukan")
    void testGetByIdFound() {
        UUID userId = UUID.randomUUID();
        UUID logId = UUID.randomUUID();
        TravelLog log = new TravelLog();
        
        when(repository.findByUserIdAndId(userId, logId)).thenReturn(Optional.of(log));

        TravelLog result = service.getById(userId, logId);

        assertNotNull(result);
        assertEquals(log, result);
    }

    @Test
    @DisplayName("GetById: User tidak ditemukan")
    void testGetByIdNotFound() {
        UUID userId = UUID.randomUUID();
        UUID logId = UUID.randomUUID();

        when(repository.findByUserIdAndId(userId, logId)).thenReturn(Optional.empty());

        TravelLog result = service.getById(userId, logId);

        assertNull(result);
    }

    @Test
    @DisplayName("Save: Menyimpan data")
    void testSave() {
        TravelLog log = new TravelLog();
        when(repository.save(log)).thenReturn(log);

        TravelLog savedLog = service.save(log);

        assertEquals(log, savedLog);
        verify(repository).save(log);
    }

    @Test
    @DisplayName("Delete: Hapus jika data ada")
    void testDeleteFound() {
        UUID userId = UUID.randomUUID();
        UUID logId = UUID.randomUUID();
        TravelLog log = new TravelLog();

        when(repository.findByUserIdAndId(userId, logId)).thenReturn(Optional.of(log));

        service.delete(userId, logId);

        verify(repository).delete(log);
    }

    @Test
    @DisplayName("Delete: Jangan hapus jika data tidak ada")
    void testDeleteNotFound() {
        UUID userId = UUID.randomUUID();
        UUID logId = UUID.randomUUID();

        when(repository.findByUserIdAndId(userId, logId)).thenReturn(Optional.empty());

        service.delete(userId, logId);

        verify(repository, never()).delete(any());
    }
}