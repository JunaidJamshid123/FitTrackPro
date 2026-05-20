# FitTrackPro — Project Requirements

A single **monolithic** Spring Boot application for tracking fitness activity. One codebase, one database, one deployable JAR.

---

## 1. Domain Overview

FitTrackPro helps users plan workouts, log sessions, track meals, and monitor progress against personal goals. Trainers create plans and get assigned to users. Memberships gate premium features.

---

## 2. Actors

- **User** — registers, manages profile, follows workout plans, logs sessions and meals, tracks progress.
- **Trainer** — creates and publishes workout plans, gets assigned to users, views their progress.
- **Admin** — manages exercise catalog, users, memberships, and views system-wide reports.

---

## 3. Core Entities

| Entity | Purpose | Key Fields |
|---|---|---|
| `User` | Registered member | id, name, email, password, age, gender, height, weight, goal, role, membershipId |
| `UserProfile` | Extended profile info | userId, activityLevel, targetWeight, targetCalories, bio |
| `Trainer` | Coach who builds plans | id, name, email, specialization, experienceYears |
| `Exercise` | Catalog item | id, name, description, muscleGroup, difficulty, equipment |
| `WorkoutPlan` | Named plan with exercises | id, name, description, trainerId, durationWeeks |
| `PlanExercise` | Plan ↔ Exercise link | planId, exerciseId, sets, reps, restSeconds, dayOfWeek |
| `WorkoutSession` | Actual performed session | id, userId, planId, sessionDate, durationMinutes, caloriesBurned |
| `SessionLog` | Per-exercise actuals | sessionId, exerciseId, setsDone, repsDone, weightUsed |
| `Meal` | Food log entry | id, userId, mealType, foodName, calories, protein, carbs, fat, loggedAt |
| `ProgressLog` | Weekly snapshot | id, userId, logDate, weight, bodyFatPercent, chest, waist, hips |
| `Membership` | Plan tier for a user | id, userId, type (FREE/PREMIUM), startDate, endDate, status |
| `Notification` | User notification | id, userId, channel, title, message, status, createdAt |
| `Assignment` | Trainer ↔ User link | trainerId, userId, assignedAt |

### Enums

- `Goal` — `WEIGHT_LOSS`, `MUSCLE_GAIN`, `MAINTENANCE`, `ENDURANCE`.
- `MuscleGroup` — `CHEST`, `BACK`, `LEGS`, `ARMS`, `SHOULDERS`, `CORE`, `FULL_BODY`.
- `Difficulty` — `BEGINNER`, `INTERMEDIATE`, `ADVANCED`.
- `MealType` — `BREAKFAST`, `LUNCH`, `DINNER`, `SNACK`.
- `Role` — `USER`, `TRAINER`, `ADMIN`.
- `MembershipType` — `FREE`, `PREMIUM`.
- `MembershipStatus` — `ACTIVE`, `EXPIRED`, `CANCELLED`.
- `NotificationChannel` — `EMAIL`, `SMS`, `PUSH`.

---

## 4. Relationships

- `User` 1—1 `UserProfile`
- `User` 1—1 `Membership`
- `User` 1—N `WorkoutSession`
- `User` 1—N `Meal`
- `User` 1—N `ProgressLog`
- `User` 1—N `Notification`
- `Trainer` 1—N `WorkoutPlan`
- `Trainer` N—N `User` (via `Assignment`)
- `WorkoutPlan` N—N `Exercise` (via `PlanExercise` with `sets`, `reps`, `restSeconds`, `dayOfWeek`)
- `WorkoutSession` 1—N `SessionLog`
- `WorkoutSession` N—1 `WorkoutPlan`

---

## 4.1 ER Diagram (ASCII)

```
 Legend:  ──1── exactly one        ──N── many
          PK = primary key   FK = foreign key   UK = unique key


                            ┌───────────────────────────┐
                            │         TRAINER           │
                            │───────────────────────────│
                            │ PK  id                    │
                            │ UK  email                 │
                            │     name                  │
                            │     specialization        │
                            │     experienceYears       │
                            │     createdAt, updatedAt  │
                            └─────┬──────────────┬──────┘
                                  │1             │1
                                  │              │
                                  │N             │N
                       ┌──────────▼─────┐   ┌────▼──────────────────┐
                       │  WORKOUT_PLAN  │   │      ASSIGNMENT       │
                       │────────────────│   │───────────────────────│
                       │ PK  id         │   │ PK  id                │
                       │ FK  trainerId  │   │ FK  trainerId         │
                       │     name       │   │ FK  userId            │
                       │     description│   │     assignedAt        │
                       │     duration   │   │ UK (trainerId,userId) │
                       │     difficulty │   └──────────┬────────────┘
                       │     goal       │              │N
                       │     published  │              │
                       └────┬─────┬─────┘              │1
                            │1    │1                   │
                            │     │                    │
                            │N    │N                   │
              ┌─────────────▼──┐  │   ┌────────────────▼──────────────┐
              │ PLAN_EXERCISE  │  │   │             USER              │
              │────────────────│  │   │───────────────────────────────│
              │ PK  id         │  │   │ PK  id                        │
              │ FK  planId     │  │   │ UK  email                     │
              │ FK  exerciseId │  │   │     name, password            │
              │     sets       │  │   │     age, gender               │
              │     reps       │  │   │     height, weight            │
              │     restSeconds│  │   │     goal, role                │
              │     dayOfWeek  │  │   │     createdAt, updatedAt      │
              │ UK (plan,ex,dow)  │   └──┬──┬──┬──┬──┬──┬──────────┬──┘
              └──────┬─────────┘  │      │1 │1 │1 │1 │1 │1         │1
                     │N           │      │  │  │  │  │  │          │
                     │            │      │N │1 │1 │N │N │N         │N
                     │1           │N     │  │  │  │  │  │          │
              ┌──────▼────────────▼──┐   │  │  │  │  │  │          │
              │       EXERCISE       │◄──┘  │  │  │  │  │          │
              │──────────────────────│      │  │  │  │  │          │
              │ PK  id               │      │  │  │  │  │          │
              │     name             │      │  │  │  │  │          │
              │     description      │      │  │  │  │  │          │
              │     muscleGroup      │      │  │  │  │  │          │
              │     difficulty       │      │  │  │  │  │          │
              │     equipment        │      │  │  │  │  │          │
              │     createdAt        │      │  │  │  │  │          │
              │     updatedAt        │      │  │  │  │  │          │
              └──────────┬───────────┘      │  │  │  │  │          │
                         │1                 │  │  │  │  │          │
                         │                  │  │  │  │  │          │
                         │N                 │  │  │  │  │          │
              ┌──────────▼──────────┐       │  │  │  │  │          │
              │     SESSION_LOG     │       │  │  │  │  │          │
              │─────────────────────│       │  │  │  │  │          │
              │ PK  id              │       │  │  │  │  │          │
              │ FK  sessionId       │◄──┐   │  │  │  │  │          │
              │ FK  exerciseId      │   │   │  │  │  │  │          │
              │     setsDone        │   │   │  │  │  │  │          │
              │     repsDone        │   │   │  │  │  │  │          │
              │     weightUsed      │   │   │  │  │  │  │          │
              └─────────────────────┘   │   │  │  │  │  │          │
                                        │N  │  │  │  │  │          │
                                        │   │  │  │  │  │          │
                                        │1  │N │1 │1 │N │N         │N
                                ┌───────┴───▼──▼──▼──▼──▼─┐  ┌─────▼────────────┐
                                │     WORKOUT_SESSION     │  │   USER_PROFILE   │
                                │─────────────────────────│  │──────────────────│
                                │ PK  id                  │  │ PK  id           │
                                │ FK  userId              │  │ FK  userId (UK)  │
                                │ FK  planId              │  │     activityLevel│
                                │     sessionDate         │  │     targetWeight │
                                │     durationMinutes     │  │     targetCalories│
                                │     caloriesBurned      │  │     bio          │
                                │     createdAt,updatedAt │  └──────────────────┘
                                └─────────────────────────┘

  ┌──────────────────────┐  ┌──────────────────────┐  ┌──────────────────────┐
  │        MEAL          │  │     PROGRESS_LOG     │  │     NOTIFICATION     │
  │──────────────────────│  │──────────────────────│  │──────────────────────│
  │ PK  id               │  │ PK  id               │  │ PK  id               │
  │ FK  userId           │  │ FK  userId           │  │ FK  userId           │
  │     mealType         │  │     logDate          │  │     channel          │
  │     foodName         │  │     weight           │  │     title            │
  │     calories         │  │     bodyFatPercent   │  │     message          │
  │     protein          │  │     chest            │  │     status           │
  │     carbs            │  │     waist            │  │     retryCount       │
  │     fat              │  │     hips             │  │     createdAt        │
  │     loggedAt         │  │     createdAt        │  └──────────────────────┘
  │     createdAt        │  │     updatedAt        │
  │     updatedAt        │  │ UK (userId,logDate)  │
  └──────────────────────┘  └──────────────────────┘
        │N                            │N                          │N
        │                             │                           │
        │1                            │1                          │1
        └────────────────► USER ◄─────┴───────────────────────────┘
                       (see above)


  ┌──────────────────────┐
  │     MEMBERSHIP       │
  │──────────────────────│
  │ PK  id               │
  │ FK  userId  (UK)     │── 1 ── 1 ── USER
  │     type             │
  │     startDate        │
  │     endDate          │
  │     status           │
  │     createdAt        │
  │     updatedAt        │
  └──────────────────────┘
```

### Relationship Summary

| From → To | Cardinality | Join Entity | Notes |
|---|---|---|---|
| User → UserProfile | 1—1 | — | `UserProfile` owns FK `user_id` (unique) |
| User → Membership | 1—1 | — | `Membership` owns FK `user_id` (unique) |
| User → WorkoutSession | 1—N | — | `user_id` FK on session |
| User → Meal | 1—N | — | `user_id` FK on meal |
| User → ProgressLog | 1—N | — | unique `(user_id, log_date)` |
| User → Notification | 1—N | — | `user_id` FK on notification |
| Trainer → WorkoutPlan | 1—N | — | `trainer_id` FK on plan |
| Trainer ↔ User | N—N | `Assignment` | unique `(trainer_id, user_id)` |
| WorkoutPlan ↔ Exercise | N—N | `PlanExercise` | carries `sets`, `reps`, `restSeconds`, `dayOfWeek`; unique `(plan_id, exercise_id, day_of_week)` |
| WorkoutPlan → WorkoutSession | 1—N | — | `plan_id` FK on session |
| WorkoutSession → SessionLog | 1—N | — | `session_id` FK on log |
| Exercise → SessionLog | 1—N | — | `exercise_id` FK on log |

---

## 5. Business Rules

### Users & Profile
- Email is unique; password stored hashed.
- Age must be ≥ 13. Height (cm) and weight (kg) must be > 0.
- BMI is derived: `weight / (heightMeters^2)`.
- Recommended daily calories computed from age, gender, weight, height, activity level, and `Goal`.

### Memberships
- Every user has exactly one active membership; new users default to `FREE`.
- `FREE` users: max 1 active workout plan, no trainer assignment, basic reports only.
- `PREMIUM` users: unlimited plans, may be assigned to a trainer, access to advanced reports.
- Upgrading FREE → PREMIUM creates a new membership with `endDate = startDate + 30 days` and marks the old one `CANCELLED` in a single transaction.
- A daily job expires memberships whose `endDate < today` and sends an expiry notification.

### Workout Plans
- Only `TRAINER` or `ADMIN` can create plans.
- A plan must contain at least 1 exercise before it can be assigned.
- Same exercise may appear on multiple days of a plan but not twice on the same `dayOfWeek`.
- A `FREE` user assigning a second plan must first deactivate the existing one.

### Sessions
- A session must reference a plan the user is currently following.
- `caloriesBurned` is auto-estimated from total `weightUsed × reps × MET-factor` if not provided.
- A session cannot be logged for a future date.
- Updating a session is allowed only within 7 days of `sessionDate`.

### Meals
- A meal entry requires positive `calories` and non-negative macros.
- Daily summary aggregates all meals for a user on a given date.
- If daily calories exceed `targetCalories × 1.2`, a warning notification is queued.

### Progress
- One `ProgressLog` per user per week (uniqueness on `userId + weekOf(logDate)`).
- Weekly summary returns: total sessions, total calories burned, total calories consumed, weight delta vs previous week.

### Trainers & Assignments
- A trainer can be assigned to many users; a user has at most one trainer at a time.
- Only PREMIUM users may have an assigned trainer.
- Reassigning replaces the existing assignment in one transaction.

### Notifications
- Notifications are produced by the system (membership expiry, calorie warning, plan assigned, milestone reached) and dispatched via `EMAIL`, `SMS`, or `PUSH`.
- Failed dispatches are retried up to 3 times before being marked `FAILED`.

### Reporting
- Top 5 most-performed exercises across all users.
- Most active users (by session count) in the last 30 days.
- Average calories burned per session by muscle group.
- Membership distribution (counts by type and status).

---

## 6. Feature List (User Stories)

### Auth & Profile
- Register a user.
- Log in and receive an auth token.
- View / update own profile.
- Compute BMI and recommended daily calories.

### Exercises (Admin)
- Create, update, delete an exercise.
- List exercises with filters: `muscleGroup`, `difficulty`, `equipment`, paginated.
- Search exercises by name.

### Workout Plans (Trainer / Admin)
- Create a plan with a list of exercises (`sets`, `reps`, `restSeconds`, `dayOfWeek`).
- Update / delete a plan (only by its creator).
- Publish / unpublish a plan.
- List published plans, filter by `difficulty` / `goal`.

### Plan Assignment
- Assign a plan to a user (subject to membership rules).
- Deactivate / switch the active plan for a user.
- List a user's active and historical plans.

### Sessions
- Log a new workout session against an active plan with per-exercise actuals.
- Update a session within the 7-day window.
- Delete a session (own data only).
- List a user's sessions, filter by date range, paginated.

### Meals
- Log a meal.
- Update / delete a meal entry.
- Daily meal summary (totals: calories, protein, carbs, fat).
- Weekly meal summary.

### Progress
- Add a weekly progress log.
- Get weekly progress summary (sessions + meals + weight delta).
- Get monthly progress trend.

### Memberships
- View current membership.
- Upgrade to PREMIUM.
- Cancel membership.
- (Admin) List expiring memberships.

### Trainers
- (Admin) Create / list trainers.
- (Trainer) View assigned users and their latest progress.
- (Admin) Assign / reassign / unassign a trainer to a user.

### Notifications
- List a user's notifications.
- Mark notifications as read.

### Admin Reports
- Top exercises, most active users, calories-per-muscle-group, membership distribution.
- Bulk deactivate users inactive for > 90 days.

---

## 7. API Surface (high-level)

Base path: `/api`

| Resource | Endpoints |
|---|---|
| Auth | `POST /auth/register`, `POST /auth/login` |
| Users | `GET /users/me`, `PUT /users/me`, `GET /users/{id}` (admin), `GET /users` (admin, filters) |
| Profile | `GET /users/me/profile`, `PUT /users/me/profile`, `GET /users/me/bmi` |
| Exercises | `GET /exercises`, `GET /exercises/{id}`, `POST /exercises` (admin), `PUT /exercises/{id}` (admin), `DELETE /exercises/{id}` (admin) |
| Plans | `GET /plans`, `GET /plans/{id}`, `POST /plans` (trainer/admin), `PUT /plans/{id}`, `DELETE /plans/{id}`, `POST /plans/{id}/publish` |
| Assignments | `POST /users/{userId}/plans/{planId}`, `DELETE /users/{userId}/plans/{planId}`, `GET /users/{userId}/plans` |
| Sessions | `POST /sessions`, `PATCH /sessions/{id}`, `DELETE /sessions/{id}`, `GET /sessions` (filters: dateFrom, dateTo) |
| Meals | `POST /meals`, `PUT /meals/{id}`, `DELETE /meals/{id}`, `GET /meals?date=`, `GET /meals/summary?date=` |
| Progress | `POST /progress`, `GET /progress?week=`, `GET /progress/trend?months=` |
| Memberships | `GET /memberships/me`, `POST /memberships/upgrade`, `POST /memberships/cancel`, `GET /memberships/expiring` (admin) |
| Trainers | `POST /trainers` (admin), `GET /trainers`, `GET /trainers/me/users`, `POST /trainers/{trainerId}/users/{userId}`, `DELETE /trainers/{trainerId}/users/{userId}` |
| Notifications | `GET /notifications`, `PATCH /notifications/{id}/read` |
| Reports | `GET /reports/top-exercises`, `GET /reports/active-users`, `GET /reports/calories-by-muscle`, `GET /reports/membership-distribution` |

All responses use `ResponseEntity<T>` with appropriate HTTP status codes and a uniform error format produced by a global exception handler.

---

## 8. Package Structure

```
com.fitness.fittrackpro
├── FittrackproApplication.java
├── config/        // beans, app configuration
├── controller/    // REST controllers
├── dto/           // request / response objects
├── exception/     // custom exceptions + global handler
├── model/         // entities + enums
├── repository/    // data access
├── security/      // auth + role checks
├── service/       // business logic
└── utils/         // calculators, mappers, constants
```

Layering rule: **Controller → Service → Repository**. Entities never cross the controller boundary; DTOs do.

---

## 9. Persistence Requirements

- Single relational database (MySQL or PostgreSQL).
- Schema managed by versioned migration files: `V1__create_users.sql`, `V2__create_exercises.sql`, etc.
- Constraints:
  - Primary keys on all tables.
  - Foreign keys across all relationships.
  - `UNIQUE` on `users.email`, `(userId, weekOf(logDate))` on progress logs.
  - `NOT NULL` on required fields.
  - `CHECK` constraints for positive weights, heights, calories, ages ≥ 13.
- Indexes:
  - `users.email`
  - `workout_sessions(user_id, session_date)`
  - `meals(user_id, logged_at)`
  - `exercises(muscle_group, difficulty)`

---

## 10. Cross-Cutting Requirements

- **Validation**: all incoming request DTOs validated (required fields, ranges, formats).
- **Transactions**: all multi-step writes (register + default membership + welcome notification; upgrade membership; reassign trainer; create plan + plan-exercise links) run in a single transaction with rollback on failure.
- **Error handling**: every custom exception maps to a stable HTTP status and a JSON error body `{ code, message, timestamp, path }`.
- **Pagination & sorting**: all list endpoints support `page`, `size`, `sort`.
- **Auditing**: `createdAt` and `updatedAt` on every entity.
- **Authorization**: role-based — `USER`, `TRAINER`, `ADMIN`; ownership checks on user-scoped resources.
- **Configuration**: DB credentials, server port, JPA settings driven by `application.properties`.

---

## 11. Build Phases

1. **Domain & in-memory** — entities, enums, services, controllers wired with in-memory stores; full REST surface working.
2. **Persistence** — introduce the database, migrations, JPA mappings, repositories; replace in-memory stores.
3. **Hardening** — validation, transactions, global error handling, pagination, role checks, reports.
4. **Polish** — DTO mapping cleanup, indexes, audit fields, final test pass.

---

## 12. Definition of Done

- App boots with a single command and serves all endpoints listed in section 7.
- Migrations build the schema from an empty database.
- All business rules in section 5 are enforced and covered by tests.
- All list endpoints support filtering, pagination, and sorting.
- Global exception handler returns a consistent error shape for every failure path.
