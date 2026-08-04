package com.utn.API_CentroDeportivo.service;

import com.utn.API_CentroDeportivo.model.dto.request.MemberRequestDTO;
import com.utn.API_CentroDeportivo.model.entity.users.User;
import com.utn.API_CentroDeportivo.model.enums.Role;

public interface IAuthService {
    void registerMember(MemberRequestDTO memberDTO);
    void createAndSaveUser(User user, Role role);
}
