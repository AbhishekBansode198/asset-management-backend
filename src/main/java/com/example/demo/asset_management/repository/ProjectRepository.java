package com.example.demo.asset_management.repository;



import com.example.demo.asset_management.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}