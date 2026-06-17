package com.fitness.fittrackpro.controller;

import com.fitness.fittrackpro.dto.profile.BmiResponse;
import com.fitness.fittrackpro.dto.profile.ProfileResponse;
import com.fitness.fittrackpro.dto.profile.UpdateProfileRequest;
import com.fitness.fittrackpro.security.CustomUserDetails;
import com.fitness.fittrackpro.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(profileService.getOwnProfile(principal.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> upsertProfile(@AuthenticationPrincipal CustomUserDetails principal,
                                                         @Valid @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(profileService.upsertOwnProfile(principal.getId(), req));
    }

    @GetMapping("/bmi")
    public ResponseEntity<BmiResponse> getBmi(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(profileService.getOwnBmi(principal.getId()));
    }
}
