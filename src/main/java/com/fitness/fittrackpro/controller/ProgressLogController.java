package com.fitness.fittrackpro.controller;

import com.fitness.fittrackpro.dto.progresslog.ProgressLogRequest;
import com.fitness.fittrackpro.dto.progresslog.ProgressLogResponse;
import com.fitness.fittrackpro.security.CustomUserDetails;
import com.fitness.fittrackpro.service.ProgressLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/progress-logs")
@RequiredArgsConstructor
public class ProgressLogController {

    private final ProgressLogService progressLogService;

    @GetMapping
    public ResponseEntity<Page<ProgressLogResponse>> getMyProgress(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 20, sort = "logDate", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(progressLogService.getUserProgress(
                principal.getId(), fromDate, toDate, pageable
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgressLogResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(progressLogService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProgressLogResponse> create(
            @Valid @RequestBody ProgressLogRequest req,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(progressLogService.create(req, principal.getUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgressLogResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProgressLogRequest req
    ) {
        return ResponseEntity.ok(progressLogService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        progressLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
