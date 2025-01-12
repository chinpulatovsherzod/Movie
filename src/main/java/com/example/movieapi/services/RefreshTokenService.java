package com.example.movieapi.services;

import com.example.movieapi.auth.RefreshToken;
import com.example.movieapi.auth.User;
import com.example.movieapi.repositories.RefreshTokenRepository;
import com.example.movieapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        RefreshToken refreshToken = user.getRefreshToken();

        if (refreshToken != null) {
            long refreshTokenValidity = 5 * 60 * 60 * 10000;
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public RefreshToken getRefreshToken(String refreshToken) {
        RefreshToken rfToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (rfToken.getExpirationTime().compareTo(Instant.now()) <0){
            refreshTokenRepository.delete(rfToken);
            throw new RuntimeException("Refresh token expired");
        }
        return rfToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken) {
        return null;
    }
}
