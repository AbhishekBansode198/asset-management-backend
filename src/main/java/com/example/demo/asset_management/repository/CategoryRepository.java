package com.example.demo.asset_management.repository;

import com.example.demo.asset_management.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}