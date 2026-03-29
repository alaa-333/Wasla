package com.example.wasla.auth.security;

import com.example.wasla.user.client.entity.Client;
import com.example.wasla.user.client.repository.ClientRepository;
import com.example.wasla.user.driver.entity.Driver;
import com.example.wasla.user.driver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom UserDetailsService that loads users from either Client or Driver tables.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final DriverRepository driverRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try to find as client first
        Client client = clientRepository.findByEmail(email).orElse(null);
        if (client != null) {
            return new User(
                    client.getEmail(),
                    client.getPassword(),
                    List.of(new SimpleGrantedAuthority("CLIENT"))
            );
        }

        // Try to find as driver
        Driver driver = driverRepository.findByEmail(email).orElse(null);
        if (driver != null) {
            return new User(
                    driver.getEmail(),
                    driver.getPassword(),
                    List.of(new SimpleGrantedAuthority("DRIVER"))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
