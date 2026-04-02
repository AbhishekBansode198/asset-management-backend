package com.example.demo.asset_management.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MachineDto {

    private Long id;

    private String machineName;

    private String serialNumber;

    private Integer availableStock;

    private Integer damagedStock;   // 🔥 IMPORTANT

    private Long categoryId;        // 🔥 for saving

    private String categoryName;    // 🔥 for display

    private String image;

    private String description;
}