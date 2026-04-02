package com.example.demo.asset_management.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String assetCode;

    private String assetName;

    private int totalQuantity;

    private String status; // PENDING, APPROVED, REJECTED
}