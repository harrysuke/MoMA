# MoMA — Android app (gym-first release)

Native Android client for the MoMA marketplace, built around the first stakeholder: a BJJ
gym that needs a Judo black belt, a Wrestler, a Kyokushin black belt and a Muay Thai coach to
staff its weekly curriculum.

The release is **gym-first**: instead of a generic "find an instructor" marketplace, the app
opens on a gym's weekly programme and the four specialist roles that programme depends on.

---

## 1. What the app does

| Area | Behaviour |
| --- | --- |
| Gym home | Coverage dashboard: which of the four specialist roles are staffed, how many credentialed candidates exist, today's sessions |
| Weekly curriculum | Mon–Sun programme with style, rule set, coach assignment and staffing state |
| Credential-gated shortlist | Hard filter on verified documents, then explainable ranking with a visible score breakdown |
| Coach profile | Bio, credentials (verified / pending), availability slots, gate result |
| Checkout | Price, 15% gym commission, coach payout, payment confirmation |
| Bookings | Active and cancelled sessions with status transitions |
| Requirement posting | Publish a new role with style, rule set, credential gate and minimum experience |
| Profile | Role switching, session info, sign out |

### The curriculum the app is modelled on

| Day | Programme | Style | Rule set |
| --- | --- | --- | --- |
| Mon | BJJ Gi — Fundamentals | BJJ Gi | IBJJF |
| Tue | BJJ No-Gi — Takedowns & Passing | BJJ No-Gi | ADCC |
| Wed | Judo for BJJ | Judo | — |
| Thu | Wrestling for No-Gi BJJ | Wrestling | ADCC |
| Fri | Striking for No-Gi BJJ | Kyokushin + Muay Thai | ACBJJ Battle Jiu-Jitsu |
| Sat | BJJ Gi Open Mat | BJJ Gi | IBJJF |
| Sun | BJJ No-Gi Open Mat | BJJ No-Gi | ADCC |

---

## 2. The credential gate

This is the product's core promise, so it is implemented as domain logic with unit tests
(`core/domain/CredentialGate.kt`).

```
evaluate(requirement, coach):
  1. style match          → fail: "teaches a different style"
  2. verified credential  → fail: "uploaded but not verified" / "no credential on file"
  3. minimum experience   → fail: "only N years"
  4. rule set experience  → fail: "no verified ADCC rule set experience"
  5. identity verified    → fail: "identity not verified"
```

A coach that fails **never enters the ranking**. Rejected coaches are still shown in the UI
with their exact rejection reason so a hiring decision can be defended.

Ranking runs only on coaches that pass the gate, with weights that sum to 100:

| Factor | Weight |
| --- | --- |
| Style | 30 |
| Credential depth (years above minimum) | 25 |
| Availability on the programme day | 15 |
| Distance | 10 |
| Rating | 10 |
| Price | 10 |

---

## 3. Project structure

```
app/src/main/java/com/moma/
├── MoMAApplication.kt          Hilt application
├── MainActivity.kt             Compose host
├── di/AppModule.kt             Room, OkHttp, Retrofit, DAO providers
├── navigation/MomaApp.kt       Routes, bottom navigation, NavHost
├── ui/theme/                   Material 3 palette + typography (light only)
├── ui/components/              Shared cards, pills, score bar, fields
├── core/
│   ├── model/Models.kt         Styles, rule sets, credentials, coaches, bookings
│   ├── domain/                 CredentialGate + CoachRanking (pure Kotlin, tested)
│   ├── database/               Room entities, DAOs, database, first-run seed
│   ├── data/                   Repositories + entity↔domain mappers
│   ├── datastore/SessionStore.kt  Keystore-backed session
│   └── network/MomaApi.kt      Retrofit contract mirroring moma_openapi.yaml
└── feature/
    ├── auth/                   Login + role selection
    ├── gym/                    Home, curriculum, shortlist, coach detail, requirement
    ├── booking/                Checkout + bookings
    └── profile/                Profile and role switching
```

Architecture follows the documented MVVM + Clean layering: Compose → ViewModel (StateFlow) →
Repository → Room / Retrofit. Money and booking state are treated as server-authoritative in
production; the local implementation keeps the same state machine
(`PENDING_PAYMENT → CONFIRMED → COMPLETED`, plus `CANCELLED`) so the UI does not change when
the API is wired in.

---

## 4. Build and run

Requirements: JDK 17+, Android SDK platform 36, Gradle wrapper included.

```bash
cd MoMA_Android_App
./gradlew :app:assembleDebug        # build
./gradlew :app:testDebugUnitTest    # run the credential-gate tests
./gradlew :app:installDebug         # install on a connected device/emulator
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

### If the test worker cannot start

On some Windows machines a malformed `PATH` entry (a directory containing a space
inside `Program Files`) makes the forked Gradle test worker fail with
`Could not find or load main class ...`. The tests themselves are fine; run them
directly:

```bash
./gradlew :app:dumpUnitTestClasspath
java -cp "$(cat app/build/unit-test-classpath.txt)" \
     org.junit.runner.JUnitCore com.moma.core.domain.CredentialGateTest
```

Expected output: `OK (11 tests)`.

### Mock vs live data

`app/build.gradle.kts` sets `BuildConfig.USE_MOCK_DATA = true` and placeholder Supabase
credentials. The app currently reads and writes Room, seeded on first launch from
`core/database/SeedData.kt`. To point at a backend, set `SUPABASE_URL` / `SUPABASE_ANON_KEY`
and swap the repository bodies for `MomaApi` calls — the DTOs already match the OpenAPI
contract.

---

## 5. Seeded demo data

Seven coaches are seeded deliberately, including two that must be rejected:

| Coach | Credential | Gate result |
| --- | --- | --- |
| Arif Rahman | Judo 3rd Dan (verified) | Passes Judo |
| Danial Hakim | BJJ black belt (verified) | Passes BJJ Gi / No-Gi |
| Rafiquddin Osman | National squad wrestling record (verified) | Passes Wrestling / ADCC |
| Kenji Sato | Kyokushin 2nd Dan (verified) | Passes Kyokushin |
| Mei Ling Tan | Certified Muay Thai coach (verified) | Passes Muay Thai; **blocked** for Kyokushin (style) |
| Hafiz Zulkifli | Judo 1st Kyu, unverified | **Blocked** — not a black belt, document pending |
| Suresh Kumar | Wrestling record, unverified | **Blocked** — document pending |

---

## 6. Not yet implemented

- Stripe / payment-provider integration (checkout confirms locally)
- Push notifications (FCM) and WorkManager outbox sync
- Coach-side workspace (listing editor, availability management, payouts)
- Admin moderation and verification queue
- Instrumented UI tests (only domain unit tests exist today)
