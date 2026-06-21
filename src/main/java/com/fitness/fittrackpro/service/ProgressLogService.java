package com.fitness.fittrackpro.service;

import com.fitness.fittrackpro.dto.progresslog.ProgressLogRequest;
import com.fitness.fittrackpro.dto.progresslog.ProgressLogResponse;
import com.fitness.fittrackpro.exception.ProgressLogNotFoundException;
import com.fitness.fittrackpro.model.ProgressLog;
import com.fitness.fittrackpro.model.User;
import com.fitness.fittrackpro.repository.ProgressLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProgressLogService {

    private final ProgressLogRepository progressLogRepository;

    @Transactional(readOnly = true)
    public Page<ProgressLogResponse> getUserProgress(Long userId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        return progressLogRepository.findByUserId(userId, fromDate, toDate, pageable)
                .map(ProgressLogResponse::from);
    }

    @Transactional(readOnly = true)
    public ProgressLogResponse getById(Long id) {
        return ProgressLogResponse.from(loadProgressLog(id));
    }

    @Transactional
    public ProgressLogResponse create(ProgressLogRequest req, User user) {
        ProgressLog log = new ProgressLog();
        apply(log, req);
        log.setUser(user);
        return ProgressLogResponse.from(progressLogRepository.save(log));
    }

    @Transactional
    public ProgressLogResponse update(Long id, ProgressLogRequest req) {
        ProgressLog log = loadProgressLog(id);
        apply(log, req);
        return ProgressLogResponse.from(log);
    }

    @Transactional
    public void delete(Long id) {
        if (!progressLogRepository.existsById(id)) {
            throw new ProgressLogNotFoundException(id);
        }
        progressLogRepository.deleteById(id);
    }

    private void apply(ProgressLog log, ProgressLogRequest req) {
        log.setLogDate(req.logDate());
        log.setWeight(req.weight());
        log.setBodyFatPercent(req.bodyFatPercent());
        log.setChest(req.chest());
        log.setWaist(req.waist());
        log.setHips(req.hips());
        log.setNotes(req.notes());
    }

    private ProgressLog loadProgressLog(Long id) {
        return progressLogRepository.findById(id)
                .orElseThrow(() -> new ProgressLogNotFoundException(id));
    }
}
