package com.utn.API_CentroDeportivo.model.repository.users;

import com.utn.API_CentroDeportivo.model.entity.users.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface ICredentialRepository extends JpaRepository<Credential, Long> {

    boolean existsByUsername(String username);
    UserDetails findByUsername(String username);
}
