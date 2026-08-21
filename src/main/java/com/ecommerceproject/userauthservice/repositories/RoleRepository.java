package com.ecommerceproject.userauthservice.repositories;

import com.ecommerceproject.userauthservice.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleTitle(String roleTitle);
}
