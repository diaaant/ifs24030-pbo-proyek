package org.delcom.app.repositories;

import org.delcom.app.entities.TravelLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TravelLogRepository extends JpaRepository<TravelLog, UUID> {
    List<TravelLog> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<TravelLog> findByUserIdAndId(UUID userId, UUID id);

    // Fitur Search (Judul atau Lokasi)
    @Query("SELECT t FROM TravelLog t WHERE t.userId = :userId AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.destination) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<TravelLog> search(UUID userId, String keyword);
}