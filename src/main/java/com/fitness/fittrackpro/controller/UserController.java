package com.fitness.fittrackpro.controller;

import com.fitness.fittrackpro.dto.auth.UserResponse;
import com.fitness.fittrackpro.dto.user.UpdateUserRequest;
import com.fitness.fittrackpro.model.enums.Goal;
import com.fitness.fittrackpro.model.enums.Role;
import com.fitness.fittrackpro.security.CustomUserDetails;
import com.fitness.fittrackpro.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(UserResponse.from(principal.getUser()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@AuthenticationPrincipal CustomUserDetails principal,
                                                 @Valid @RequestBody UpdateUserRequest req) {
        return ResponseEntity.ok(userService.updateOwnProfile(principal.getId(), req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> list(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Goal goal,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(userService.search(role, goal, q, pageable));
    }
}
