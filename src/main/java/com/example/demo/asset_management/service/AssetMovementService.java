package com.example.demo.asset_management.service;

import com.example.demo.asset_management.entity.*;
import com.example.demo.asset_management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AssetMovementService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AssetMovementRepository movementRepository;

    private static final String HOME_LOCATION = "STORE Room Pune Office";

    // ✅ STATUS CONSTANTS
    private static final String PENDING = "PENDING";
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";

    // ✅ CREATE MOVEMENT
    public AssetMovement createMovement(Long assetId,
                                        Long projectId,
                                        Long fromProjectId,
                                        Long toProjectId,
                                        int quantity,
                                        String movementType) {

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        Project project = (projectId != null)
                ? projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"))
                : null;

        Project fromProject = (fromProjectId != null)
                ? projectRepository.findById(fromProjectId)
                .orElseThrow(() -> new RuntimeException("fromProject not found"))
                : null;

        Project toProject = (toProjectId != null)
                ? projectRepository.findById(toProjectId)
                .orElseThrow(() -> new RuntimeException("toProject not found"))
                : null;

        if ("OUT".equals(movementType)) {
            if (project == null) {
                throw new RuntimeException("Project is required for OUT");
            }

            int available = calculateStoreQuantity(asset);
            if (quantity > available) {
                throw new RuntimeException("Not enough stock in store");
            }
        }

        if ("IN".equals(movementType)) {
            if (project == null) {
                throw new RuntimeException("Project is required for IN");
            }

            int available = calculateProjectQuantity(asset, project);
            if (quantity > available) {
                throw new RuntimeException("Not enough stock in project");
            }
        }

        if ("TRANSFER".equals(movementType)) {

            if (fromProject == null || toProject == null) {
                throw new RuntimeException("Both fromProject and toProject required");
            }

            if (fromProject.getId().equals(toProject.getId())) {
                throw new RuntimeException("Cannot transfer to same project");
            }

            int available = calculateProjectQuantity(asset, fromProject);
            if (quantity > available) {
                throw new RuntimeException("Not enough stock in source project");
            }
        }

        AssetMovement movement = new AssetMovement();
        movement.setAsset(asset);
        movement.setProject(project);
        movement.setFromProject(fromProject);
        movement.setToProject(toProject);
        movement.setQuantity(quantity);
        movement.setMovementType(movementType);
        movement.setStatus(PENDING);
        movement.setCreatedAt(java.time.LocalDateTime.now());

        return movementRepository.save(movement);
    }

    // ✅ NEW ADDED METHOD FOR PENDING REQUESTS
    public List<AssetMovement> getPendingRequests() {
        return movementRepository.findByStatus(PENDING);
    }

    // ✅ APPROVE
    public AssetMovement approveMovement(Long id) {
        AssetMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movement not found"));

        movement.setStatus(APPROVED);
        movement.setApprovedAt(java.time.LocalDateTime.now());

        return movementRepository.save(movement);
    }

    // ✅ REJECT
    public AssetMovement rejectMovement(Long id) {
        AssetMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movement not found"));

        movement.setStatus(REJECTED);
        return movementRepository.save(movement);
    }

    // ✅ STORE QUANTITY
    public int calculateStoreQuantity(Asset asset) {

        int totalIn = movementRepository.getTotalInApproved(asset.getId());
        int totalOut = movementRepository.getTotalOutApproved(asset.getId());

        return asset.getTotalQuantity() + totalIn - totalOut;
    }

    // ✅ PROJECT QUANTITY
    public int calculateProjectQuantity(Asset asset, Project project) {

        List<AssetMovement> list =
                movementRepository.findByAssetAndStatus(asset, APPROVED);

        int qty = 0;

        for (AssetMovement m : list) {

            if ("OUT".equals(m.getMovementType()) && project.equals(m.getProject())) {
                qty += m.getQuantity();
            }

            if ("IN".equals(m.getMovementType()) && project.equals(m.getProject())) {
                qty -= m.getQuantity();
            }

            if ("TRANSFER".equals(m.getMovementType())) {

                if (project.equals(m.getToProject())) qty += m.getQuantity();
                if (project.equals(m.getFromProject())) qty -= m.getQuantity();
            }
        }

        return qty;
    }

    // ✅ HISTORY
    public List<AssetMovement> getAssetHistory(Long assetId) {
        return movementRepository
                .findByAssetIdAndStatusOrderByCreatedAtAsc(assetId, APPROVED);
    }

    public List<String> getAssetFullHistory(Long assetId) {

        List<AssetMovement> movements =
                movementRepository.findByAssetIdAndStatusOrderByCreatedAtAsc(assetId, APPROVED);

        List<String> history = new java.util.ArrayList<>();

        for (AssetMovement m : movements) {

            String record = "";

            if ("OUT".equals(m.getMovementType())) {
                record = m.getCreatedAt() + " → OUT from STORE to Project: "
                        + m.getProject().getProjectName()
                        + " | Qty: " + m.getQuantity();
            } else if ("IN".equals(m.getMovementType())) {
                record = m.getCreatedAt() + " → RETURNED to STORE from Project: "
                        + m.getProject().getProjectName()
                        + " | Qty: " + m.getQuantity();
            } else if ("TRANSFER".equals(m.getMovementType())) {
                record = m.getCreatedAt() + " → TRANSFER from "
                        + m.getFromProject().getProjectName()
                        + " to "
                        + m.getToProject().getProjectName()
                        + " | Qty: " + m.getQuantity();
            }

            history.add(record);
        }

        return history;
    }

    public List<AssetMovement> getProjectHistory(Long projectId) {
        return movementRepository.findByProjectId(projectId);
    }

    public Map<String, String> getDispatchLocations(Long assetId) {
        List<AssetMovement> movements = movementRepository.findByAssetIdOrderByCreatedAtDesc(assetId);

        String lastLocation = movements.isEmpty() ? HOME_LOCATION : getLastLocationFromMovement(movements.get(0));

        return Map.of(
                "homeLocation", HOME_LOCATION,
                "lastLocation", lastLocation
        );
    }

    private String getLastLocationFromMovement(AssetMovement movement) {
        switch (movement.getMovementType()) {
            case "IN":
                return movement.getProject() != null ? movement.getProject().getProjectName() : HOME_LOCATION;
            case "OUT":
                return HOME_LOCATION;
            case "TRANSFER":
                return movement.getToProject() != null ? movement.getToProject().getProjectName() : HOME_LOCATION;
            default:
                return HOME_LOCATION;
        }
    }
}