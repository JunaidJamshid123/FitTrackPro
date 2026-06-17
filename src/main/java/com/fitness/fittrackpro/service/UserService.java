package com.fitness.fittrackpro.service;

import com.fitness.fittrackpro.dto.auth.UserResponse;
import com.fitness.fittrackpro.dto.user.UpdateUserRequest;
import com.fitness.fittrackpro.exception.UserNotFoundException;
import com.fitness.fittrackpro.model.User;
import com.fitness.fittrackpro.model.enums.Goal;
import com.fitness.fittrackpro.model.enums.Role;
import com.fitness.fittrackpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return UserResponse.from(loadUser(id));
    }

    @Transactional
    public UserResponse updateOwnProfile(Long userId, UpdateUserRequest req) {
        User user = loadUser(userId);
        user.setName(req.name().trim());
        user.setAge(req.age());
        user.setGender(req.gender());
        user.setHeight(req.height());
        user.setWeight(req.weight());
        user.setGoal(req.goal());
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> search(Role role, Goal goal, String q, Pageable pageable) {
        String trimmed = (q == null || q.isBlank()) ? null : q.trim();
        return userRepository.search(role, goal, trimmed, pageable)
                .map(UserResponse::from);
    }

    private User loadUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
