package org.delcom.app.dto;

import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public class TravelLogForm {
    private UUID id;
    private String title;
    private String destination;
    private String description;
    private Double totalCost;
    private Integer rating;
    private MultipartFile imageFile; // Untuk upload

    // Constructor, Getter, Setter
    public TravelLogForm() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public MultipartFile getImageFile() { return imageFile; }
    public void setImageFile(MultipartFile imageFile) { this.imageFile = imageFile; }
}