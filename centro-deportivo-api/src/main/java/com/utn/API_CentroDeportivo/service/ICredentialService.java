package com.utn.API_CentroDeportivo.service;

import com.utn.API_CentroDeportivo.model.entity.users.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ICredentialService extends UserDetailsService {
    boolean existsByUsername(String username);
    User getUserByUsername(String username);
}
