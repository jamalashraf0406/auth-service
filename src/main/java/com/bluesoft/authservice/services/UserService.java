package com.bluesoft.authservice.services;

import com.bluesoft.authservice.entities.Role;
import com.bluesoft.authservice.entities.User;
import com.bluesoft.authservice.enums.RoleName;
import com.bluesoft.authservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsManager {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder bcryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(mapRoles(user.getRoles()))
                .disabled(!user.isEnabled())
                .build();
    }

    @Override
    public void createUser(UserDetails userRequest) {

        Optional<User> user = userRepository.findByUsername(userRequest.getUsername());
        if (user.isPresent()) {
            throw new IllegalArgumentException("Username is already used");
        }

        Set<Role> roles = Set.of(
                roleService.createRole(
                        resolveRoles(userRequest.getAuthorities()).stream().toList().get(0).getName()));

        User newUser = User.builder()
                .username(userRequest.getUsername())
                .password(bcryptPasswordEncoder.encode(userRequest.getPassword()))
                .roles(roles)
                .enabled(true)
                .build();

        userRepository.save(newUser);
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private Collection<? extends GrantedAuthority> mapRoles(Set<Role> roles) {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.getName().name()))
                .toList();
    }

    private Set<Role> resolveRoles(Collection<? extends GrantedAuthority> authorities) {
        Set<Role> roles = new HashSet<>();

        for (GrantedAuthority authority : authorities) {
            RoleName roleName = RoleName.valueOf(authority.getAuthority());
            roles.add(Role.builder().name(roleName).build());
        }
        return roles;
    }
}