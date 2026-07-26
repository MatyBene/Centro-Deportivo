package com.utn.API_CentroDeportivo.model.mapper;

import com.utn.API_CentroDeportivo.model.dto.request.CredentialRequestDTO;
import com.utn.API_CentroDeportivo.model.entity.users.Credential;
import com.utn.API_CentroDeportivo.model.entity.users.User;

public class CredentialMapper {

    public static Credential mapToCredential(CredentialRequestDTO credentialDTO, User user) {
        Credential credential = new Credential();
        credential.setUsername(credentialDTO.getUsername());
        credential.setPassword(credentialDTO.getPassword());
//        credential.setRole(credentialDTO.getRole());
        credential.setUser(user);
        return credential;
    }
}
