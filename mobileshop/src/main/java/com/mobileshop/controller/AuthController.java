package com.mobileshop.controller;

import com.mobileshop.dto.*;
import com.mobileshop.entity.*;
import com.mobileshop.repository.*;
import com.mobileshop.security.JwtService;
import com.mobileshop.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository               userRepository;
    private final TenantRepository             tenantRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final JwtService                   jwtService;
    private final PasswordEncoder              passwordEncoder;
    private final EmailService                 emailService;

    // ── Login ──
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!user.isActive())
            throw new RuntimeException("Your account has been deactivated. Contact admin.");

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid email or password");

        Tenant tenant = tenantRepository.findByTenantId(user.getTenantId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        if (!tenant.isActive())
            throw new RuntimeException(
                    "Your shop account is deactivated. Contact admin.");

        String token = jwtService.generateToken(user);

        return LoginResponseDTO.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .tenantId(user.getTenantId())
                .shopName(tenant.getShopName())
                .shopAddress(tenant.getAddress() + ", " + tenant.getCity())
                .shopPhone(tenant.getPhone())
                .build();
    }

    // ── Forgot Password ──
    @PostMapping("/forgot-password")
    @Transactional
    public String forgotPassword(@RequestBody ForgotPasswordDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("No account found with this email"));

        if (!user.isActive())
            throw new RuntimeException(
                    "Account is deactivated. Contact admin.");

        // delete old tokens for this email
        resetTokenRepository.deleteByEmail(dto.getEmail());

        // generate secure token
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .email(dto.getEmail())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        resetTokenRepository.save(resetToken);

        // send email async
        emailService.sendPasswordResetEmail(
                dto.getEmail(), user.getName(), token);

        return "Password reset link sent to " + dto.getEmail();
    }

    // ── Validate Reset Token ──
    @GetMapping("/reset-password/validate")
    public String validateToken(@RequestParam String token) {
        PasswordResetToken resetToken = resetTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid or expired reset link"));

        if (resetToken.isUsed())
            throw new RuntimeException(
                    "This reset link has already been used");

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException(
                    "Reset link has expired. Please request a new one.");

        return "valid";
    }

    // ── Reset Password ──
    @PostMapping("/reset-password")
    @Transactional
    public String resetPassword(@RequestBody ResetPasswordDTO dto) {

        if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
            throw new RuntimeException("Passwords do not match");

        if (dto.getNewPassword().length() < 8)
            throw new RuntimeException(
                    "Password must be at least 8 characters");

        PasswordResetToken resetToken = resetTokenRepository
                .findByToken(dto.getToken())
                .orElseThrow(() ->
                        new RuntimeException("Invalid or expired reset link"));

        if (resetToken.isUsed())
            throw new RuntimeException(
                    "This reset link has already been used");

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException(
                    "Reset link expired. Please request a new one.");

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);

        return "Password reset successfully. You can now login.";
    }
}