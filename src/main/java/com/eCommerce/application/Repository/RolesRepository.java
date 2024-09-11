package com.eCommerce.application.Repository;

import com.eCommerce.application.Entity.AppRoles;
import com.eCommerce.application.Entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByRoleName(AppRoles appRoles);
}
