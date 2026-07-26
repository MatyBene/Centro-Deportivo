package com.utn.API_CentroDeportivo.model.repository.users;

import com.utn.API_CentroDeportivo.model.entity.User;
import com.utn.API_CentroDeportivo.model.enums.PermissionLevel;
import com.utn.API_CentroDeportivo.model.enums.Role;
import com.utn.API_CentroDeportivo.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IUserRepository extends JpaRepository<User, Long> {
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.credential c WHERE "
            + "(:role IS NULL OR c.role = :role) AND "
            + "(:status IS NULL OR u.status = :status) AND "
            + "(:permission IS NULL OR u.permissionLevel = :permission)")
    Page<User> findUsersByFilters(@Param("role") Role role,
                                  @Param("status") Status status,
                                  @Param("permission") PermissionLevel permission,
                                  Pageable pageable);
}
