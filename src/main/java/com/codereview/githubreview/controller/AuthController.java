package com.codereview.githubreview.controller;

import com.codereview.githubreview.dto.AuthResponse;
import com.codereview.githubreview.dto.LoginRequest;
import com.codereview.githubreview.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authManager, JwtService jwt, UserDetailsService uds) {
        this.authenticationManager = authManager;
        this.jwtService = jwt;
        this.userDetailsService = uds;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        // Spring Security ke through verify karo
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Success hone par Token generate karo
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwt = jwtService.generateToken(userDetails);

        // Purani line: return AuthResponse.builder().accessToken(jwt).build();
        return new AuthResponse(jwt, null, null);
    }
}