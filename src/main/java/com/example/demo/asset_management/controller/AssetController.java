package com.example.demo.asset_management.controller;

import com.example.demo.asset_management.entity.Asset;
import com.example.demo.asset_management.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assets")
@CrossOrigin(origins = "*")
public class AssetController {

    @Autowired
    private AssetService assetService;

    // ✅ CREATE
    @PostMapping
    public Asset create(@RequestBody Asset asset) {
        return assetService.createAsset(asset);
    }

    // ✅ GET ALL
    @GetMapping
    public List<Asset> getAll() {
        return assetService.getAllAssets();
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public Asset getById(@PathVariable Long id) {
        return assetService.getAssetById(id);
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return "Asset deleted successfully";
    }
}