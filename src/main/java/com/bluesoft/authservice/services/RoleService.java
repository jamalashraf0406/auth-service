package com.bluesoft.authservice.services;

import com.bluesoft.authservice.entities.Role;
import com.bluesoft.authservice.enums.RoleName;
import com.bluesoft.authservice.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .name(roleName)
                                        .build()
                        ));
    }
}
