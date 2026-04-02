package com.example.demo.asset_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "asset_movements")
@JsonInclude(JsonInclude.Include.NON_NULL) // 🔥 hides null fields in response
public class AssetMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Asset reference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Asset asset;

    // 🔹 Used for IN / OUT
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Project project;

    // 🔹 Used for TRANSFER (FROM)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_project_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Project fromProject;

    // 🔹 Used for TRANSFER (TO)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_project_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Project toProject;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String movementType; // IN / OUT / TRANSFER

    @Column(nullable = false)
    private String status; // PENDING / APPROVED / REJECTED

    // 🔥 CREATED TIME
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 🔥 APPROVED TIME (optional)
    private LocalDateTime approvedAt;

    // 🔥 AUTO SET CREATED TIME
    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }
}