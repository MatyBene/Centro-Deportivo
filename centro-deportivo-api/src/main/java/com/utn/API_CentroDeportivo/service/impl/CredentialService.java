package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.entity.users.Credential;
import com.utn.API_CentroDeportivo.model.entity.users.User;
import com.utn.API_CentroDeportivo.model.repository.users.ICredentialRepository;
import com.utn.API_CentroDeportivo.service.ICredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CredentialService implements ICredentialService {

    @Autowired
    private ICredentialRepository credentialRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDetails user = credentialRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("El nombre de usuario no existe: " + username);
        }
        return user;
    }

    @Override
    public boolean existsByUsername(String username) {
        return credentialRepository.existsByUsername(username);
    }

    @Override
    public User getUserByUsername(String username) {
        Credential userCred = (Credential) credentialRepository.findByUsername(username);
        return userCred.getUser();
    }
}
