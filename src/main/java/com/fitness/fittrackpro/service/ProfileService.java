package com.fitness.fittrackpro.service;

import com.fitness.fittrackpro.dto.profile.BmiResponse;
import com.fitness.fittrackpro.dto.profile.ProfileResponse;
import com.fitness.fittrackpro.dto.profile.UpdateProfileRequest;
import com.fitness.fittrackpro.exception.ProfileNotFoundException;
import com.fitness.fittrackpro.exception.UserNotFoundException;
import com.fitness.fittrackpro.model.User;
import com.fitness.fittrackpro.model.UserProfile;
import com.fitness.fittrackpro.model.enums.ActivityLevel;
import com.fitness.fittrackpro.repository.UserProfileRepository;
import com.fitness.fittrackpro.repository.UserRepository;
import com.fitness.fittrackpro.utils.FitnessCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getOwnProfile(Long userId) {
        UserProfile p = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));
        return ProfileResponse.from(p);
    }

    /** Upsert: creates the profile on first write, updates it thereafter. */
    @Transactional
    public ProfileResponse upsertOwnProfile(Long userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserProfile profile = profileRepository.findByUser(user).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUser(user);
            return p;
        });

        profile.setActivityLevel(req.activityLevel());
        profile.setTargetWeight(req.targetWeight());
        profile.setTargetCalories(req.targetCalories());
        profile.setBio(req.bio());

        profile = profileRepository.save(profile);
        return ProfileResponse.from(profile);
    }

    @Transactional(readOnly = true)
    public BmiResponse getOwnBmi(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        double bmi = FitnessCalculator.bmi(user.getWeight(), user.getHeight());
        String category = FitnessCalculator.bmiCategory(bmi);

        // If a profile exists use its activityLevel; otherwise fall back to SEDENTARY so the
        // calorie estimate is still useful before the user has filled out their profile.
        ActivityLevel activity = profileRepository.findByUser(user)
                .map(UserProfile::getActivityLevel)
                .orElse(ActivityLevel.SEDENTARY);

        int calories = FitnessCalculator.recommendedDailyCalories(
                user.getAge(), user.getGender(), user.getHeight(),
                user.getWeight(), activity, user.getGoal());

        return new BmiResponse(user.getHeight(), user.getWeight(), bmi, category, calories);
    }
}
