package com.nurdorproject.auth_service.service;

import com.nurdorproject.auth_service.model.Volunteer;
import com.nurdorproject.auth_service.repository.VolunteerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VolunteerDetailsService implements UserDetailsService {

    private VolunteerRepository volunteerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Volunteer volunteer = volunteerRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Volunteer not found: " + username));
        String roleName = mapRoleIdToRole(volunteer.getVolunteerRole());

        return User.builder()
                .username(volunteer.getUsername())
                .password(volunteer.getPassword())
                .authorities(new SimpleGrantedAuthority(roleName))
                .build();
    }

    private String mapRoleIdToRole(int roleId) {
        return switch(roleId) {
            case 1 -> "ROLE_ADMIN";
            case 2 -> "ROLE_VOLUNTEER";
            default -> "ROLE_GUEST";
        };
    }
}
