package org.delcom.app.services;

import org.delcom.app.entities.TravelLog;
import org.delcom.app.repositories.TravelLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class TravelLogService {
    private final TravelLogRepository repository;

    public TravelLogService(TravelLogRepository repository) {
        this.repository = repository;
    }

    public List<TravelLog> getAll(UUID userId, String keyword) {
        if (keyword != null && !keyword.isBlank()) return repository.search(userId, keyword);
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public TravelLog getById(UUID userId, UUID id) {
        return repository.findByUserIdAndId(userId, id).orElse(null);
    }

    @Transactional
    public TravelLog save(TravelLog log) {
        return repository.save(log);
    }

    @Transactional
    public void delete(UUID userId, UUID id) {
        TravelLog log = getById(userId, id);
        if(log != null) repository.delete(log);
    }
}