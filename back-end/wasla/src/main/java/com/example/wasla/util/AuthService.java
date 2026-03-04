package com.example.wasla.util;

import com.example.wasla.dto.request.LoginRequest;
import com.example.wasla.dto.request.ClientRegisterRequest;
import com.example.wasla.dto.request.DriverRegisterRequest;
import com.example.wasla.dto.response.TokenResponse;
import com.example.wasla.mapper.UserMapper;
import com.example.wasla.model.DriverProfile;
import com.example.wasla.model.User;
import com.example.wasla.model.enums.Role;
import com.example.wasla.model.enums.VehicleType;
import com.example.wasla.repository.DriverProfileRepository;
import com.example.wasla.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final DriverProfileRepository driverProfileRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenResponse registerClient(ClientRegisterRequest request) {
        User savedUser = createAndSaveUser(request.getFullName(), request.getEmail(), request.getPassword(), request.getPhone(), Role.CLIENT);
        return generateTokenResponse(savedUser);
    }

    @Transactional
    public TokenResponse registerDriver(DriverRegisterRequest request) {
        User savedUser = createAndSaveUser(request.getFullName(), request.getEmail(), request.getPassword(), request.getPhone(), Role.DRIVER);

        // Driver-specific logic
        DriverProfile driverProfile = DriverProfile.builder()
                .vehicleType(VehicleType.valueOf(request.getTruckType()))
                .licensePlate(request.getLicensePlate())
                .user(savedUser)
                .isAvailable(true)
                .currentLat(90.22)
                .currentLng(66.5)
                .build();
        driverProfileRepository.save(driverProfile);

        return generateTokenResponse(savedUser);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        var token = jwtService.generateToken(userDetails);

        return TokenResponse.builder()
                .accessToken(token)
                .expirationIn(jwtService.getExpiration().toString())
                .subject(userDetails.getUsername())
                .build();

    }



    // helper method
    private User createAndSaveUser(String name, String email, String password, String phone, Role role) {
        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(password)); // Apply encoding here!
        return userRepository.save(user);
    }

    private TokenResponse generateTokenResponse(User user) {
        var token = jwtService.generateToken(user);
        return TokenResponse.builder()
                .accessToken(token)
                .expirationIn(jwtService.getExpiration().toString())
                .subject(user.getFullName())
                .build();
    }

}
