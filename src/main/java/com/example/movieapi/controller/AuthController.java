package com.example.movieapi.controller;


import com.example.movieapi.auth.RefreshToken;
import com.example.movieapi.auth.User;
import com.example.movieapi.auth.utils.AuthResponse;
import com.example.movieapi.auth.utils.LoginRequest;
import com.example.movieapi.auth.utils.RefreshTokenRequest;
import com.example.movieapi.auth.utils.RegisterRequest;
import com.example.movieapi.service.AuthService;
import com.example.movieapi.services.JwtService;
import com.example.movieapi.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
       RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
       User user = refreshToken.getUser();

       String accessToken = jwtService.generateToken(user);
       return ResponseEntity.ok(AuthResponse.builder()
               .accessToken(accessToken)
               .refreshToken(refreshToken.getRefreshToken())
               .build());
    }
}
