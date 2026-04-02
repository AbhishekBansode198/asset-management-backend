package com.example.demo.asset_management.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExitDto {

    private Long id;
    private Integer quantity;
    private Long machineId;
}