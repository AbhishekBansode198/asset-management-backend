package com.example.demo.asset_management.dto;

public class StockReportDto {

    private Long machineId;
    private String machineName;
    private Integer availableStock;

    public StockReportDto() {
    }

    public StockReportDto(Long machineId, String machineName, Integer availableStock) {
        this.machineId = machineId;
        this.machineName = machineName;
        this.availableStock = availableStock;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }
}