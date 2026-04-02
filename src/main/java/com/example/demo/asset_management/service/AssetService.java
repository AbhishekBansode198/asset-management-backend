package com.example.demo.asset_management.service;

import com.example.demo.asset_management.entity.Asset;
import com.example.demo.asset_management.repository.AssetMovementRepository;
import com.example.demo.asset_management.repository.AssetRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {

    private final AssetMovementRepository movementRepository;
    private final AssetRepository assetRepository;

    public AssetService(AssetMovementRepository movementRepository,
                        AssetRepository assetRepository) {
        this.movementRepository = movementRepository;
        this.assetRepository = assetRepository;
    }

    // ✅ CREATE ASSET
    public Asset createAsset(Asset asset) {

        // prevent duplicate assetCode
        assetRepository.findByAssetCode(asset.getAssetCode())
                .ifPresent(a -> {
                    throw new RuntimeException("Asset code already exists");
                });

        return assetRepository.save(asset);
    }

    // ✅ GET ALL
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    // ✅ GET BY ID
    public Asset getAssetById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
    }

    // 🔥 FIXED STORE QUANTITY (ONLY APPROVED DATA)
    public int calculateStoreQuantity(Asset asset) {

        int totalIn = movementRepository.getTotalInApproved(asset.getId());
        int totalOut = movementRepository.getTotalOutApproved(asset.getId());

        // ✅ FINAL LOGIC (CORRECT)
        return asset.getTotalQuantity() + totalIn - totalOut;
    }

    // ✅ DELETE
    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }
}