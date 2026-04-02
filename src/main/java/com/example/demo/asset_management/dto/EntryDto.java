package com.example.demo.asset_management.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntryDto {

    private Long id;
    private Integer quantity;
    private Long machineId;
}