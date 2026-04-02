package com.example.demo.asset_management.repository;

import com.example.demo.asset_management.entity.Asset;
import com.example.demo.asset_management.entity.AssetMovement;
import com.example.demo.asset_management.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssetMovementRepository extends JpaRepository<AssetMovement, Long> {

    // 🔥 Get all approved movements for an asset
    List<AssetMovement> findByAssetAndStatus(Asset asset, String status);

    // 🔥 History sorted by time (VERY IMPORTANT)
    List<AssetMovement> findByAssetIdAndStatusOrderByCreatedAtAsc(Long assetId, String status);

    List<AssetMovement> findByAssetIdOrderByCreatedAtDesc(Long assetId);
    // 🔥 Get all movements where asset moved FROM a project
    List<AssetMovement> findByFromProjectAndStatus(Project project, String status);

    // 🔥 Get all movements where asset moved TO a project
    List<AssetMovement> findByToProjectAndStatus(Project project, String status);

    // 🔥 Combined project history (IN + OUT + TRANSFER)
    List<AssetMovement> findByProjectOrFromProjectOrToProject(
            Project project,
            Project fromProject,
            Project toProject
    );
    List<AssetMovement> findByProjectId(Long projectId);
    List<AssetMovement> findByStatus(String status);

    // 🔥 Optional: get all movements for a project (approved only)
    List<AssetMovement> findByStatusAndFromProjectOrToProject(
            String status,
            Project fromProject,
            Project toProject
    );

    @Query("SELECT COALESCE(SUM(m.quantity),0) FROM AssetMovement m WHERE m.asset.id = :assetId AND m.movementType = 'IN' AND m.status = 'APPROVED'")
    int getTotalInApproved(Long assetId);

    @Query("SELECT COALESCE(SUM(m.quantity),0) FROM AssetMovement m WHERE m.asset.id = :assetId AND m.movementType = 'OUT' AND m.status = 'APPROVED'")
    int getTotalOutApproved(Long assetId);
}