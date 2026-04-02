package com.example.demo.asset_management.controller;

import com.example.demo.asset_management.entity.Asset;
import com.example.demo.asset_management.entity.AssetMovement;
import com.example.demo.asset_management.repository.AssetRepository;
import com.example.demo.asset_management.service.AssetMovementService;

import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movement")
public class AssetMovementController {

    private final AssetMovementService service;
    private final AssetRepository assetRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ✅ SINGLE CONSTRUCTOR
    public AssetMovementController(
            AssetMovementService service,
            AssetRepository assetRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.service = service;
        this.assetRepository = assetRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // ✅ CREATE MOVEMENT + 🔔 NOTIFICATION (FIXED SAFE VERSION)
    @PostMapping
    public AssetMovement createMovement(@RequestBody Map<String, Object> request) {

        if (request.get("assetId") == null || request.get("movementType") == null) {
            throw new RuntimeException("assetId and movementType are required");
        }

        // ✅ SAFE PARSING
        Long assetId = Long.valueOf(request.get("assetId").toString());

        Long projectId = null;
        if (request.get("projectId") != null) {
            String value = request.get("projectId").toString().trim();
            if (!value.isEmpty()) {
                projectId = Long.valueOf(value);
            }
        }

        Long fromProjectId = null;
        if (request.get("fromProjectId") != null) {
            String value = request.get("fromProjectId").toString().trim();
            if (!value.isEmpty()) {
                fromProjectId = Long.valueOf(value);
            }
        }

        Long toProjectId = null;
        if (request.get("toProjectId") != null) {
            String value = request.get("toProjectId").toString().trim();
            if (!value.isEmpty()) {
                toProjectId = Long.valueOf(value);
            }
        }

        int quantity = 0;
        if (request.get("quantity") != null) {
            String value = request.get("quantity").toString().trim();
            if (!value.isEmpty()) {
                quantity = Integer.parseInt(value);
            }
        }

        String movementType = request.get("movementType").toString();

        // ✅ SERVICE CALL
        AssetMovement movement = service.createMovement(
                assetId,
                projectId,
                fromProjectId,
                toProjectId,
                quantity,
                movementType
        );

        // 🔔 REALTIME NOTIFICATION
        messagingTemplate.convertAndSend(
                "/topic/notifications",
                movement
        );

        return movement;
    }

    // ✅ APPROVE + 🔔 NOTIFICATION
    @PutMapping("/{id}/approve")
    public AssetMovement approve(@PathVariable Long id) {
        AssetMovement m = service.approveMovement(id);

        messagingTemplate.convertAndSend(
                "/topic/notifications",
                "APPROVED ID: " + id
        );

        return m;
    }

    // ✅ REJECT + 🔔 NOTIFICATION
    @PutMapping("/{id}/reject")
    public AssetMovement reject(@PathVariable Long id) {
        AssetMovement m = service.rejectMovement(id);

        messagingTemplate.convertAndSend(
                "/topic/notifications",
                "REJECTED ID: " + id
        );

        return m;
    }
    @GetMapping("/pending")
    public List<AssetMovement> getPendingRequests() {
        return service.getPendingRequests();
    }
    // 🔥 HISTORY
    @GetMapping("/history/{assetId}")
    public List<AssetMovement> getHistory(@PathVariable Long assetId) {
        return service.getAssetHistory(assetId);
    }

    // ✅ PROJECT HISTORY
    @GetMapping("/project-history/{projectId}")
    public List<AssetMovement> getProjectHistory(@PathVariable Long projectId) {
        return service.getProjectHistory(projectId);
    }

    // ✅ STORE STOCK
    @GetMapping("/store/{assetId}")
    public int getStoreStock(@PathVariable Long assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        return service.calculateStoreQuantity(asset);
    }

    // ✅ FULL HISTORY
    @GetMapping("/full-history/{assetId}")
    public List<String> getFullHistory(@PathVariable Long assetId) {
        return service.getAssetFullHistory(assetId);
    }

    // ✅ DISPATCH LOCATION
    @GetMapping("/dispatch-locations/{assetId}")
    public Map<String, String> getDispatchLocations(@PathVariable Long assetId) {
        return service.getDispatchLocations(assetId);
    }

}