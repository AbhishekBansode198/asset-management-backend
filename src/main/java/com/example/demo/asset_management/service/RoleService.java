package com.example.demo.asset_management.service;



import com.example.demo.asset_management.entity.Role;
import com.example.demo.asset_management.entity.User;
import com.example.demo.asset_management.repository.RoleRepository;
import com.example.demo.asset_management.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {


    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }


    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }


    public Role getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }


    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    public User createUser(User user) {

        if (user.getRole() != null && user.getRole().getId() != null) {

            Role role = roleRepository.findById(user.getRole().getId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            user.setRole(role);
        }

        return userRepository.save(user);
    }
}