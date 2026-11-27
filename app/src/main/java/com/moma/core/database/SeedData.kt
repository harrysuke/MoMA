package com.moma.core.database

/**
 * First-run seed for the gym-first MVP.
 *
 * The seed mirrors the documented BJJ gym curriculum: Gi and No-Gi BJJ on
 * Monday/Tuesday, Judo for BJJ on Wednesday, Wrestling for No-Gi BJJ under the
 * ADCC rule set on Thursday, striking for No-Gi BJJ under the ACBJJ Battle rule
 * set on Friday, and Gi / No-Gi open mats at the weekend.
 *
 * Coaches are seeded with a mix of verified and unverified credentials so the
 * credential gate has something real to reject.
 */
object SeedData {

    const val GYM_ID = "gym-axis-bjj"

    fun gym() = GymEntity(
        id = GYM_ID,
        name = "Axis BJJ Academy",
        city = "Johor Bahru",
        address = "18 Jalan Serampang, Taman Sri Tebrau",
        tagline = "BJJ-first academy with a full grappling and striking curriculum",
        verified = true
    )

    fun programs() = listOf(
        ProgramEntity(
            id = "prog-mon-gi",
            gymId = GYM_ID,
            dayIndex = 1,
            styleId = "bjj_gi",
            title = "BJJ Gi — Fundamentals",
            focus = "Guard retention, positional sparring and Gi grips",
            ruleSetId = "ibjjf",
            startTime = "19:00",
            endTime = "20:30",
            level = "All levels",
            coachRole = "BJJ Gi Coach",
            assignedCoachId = "coach-danial",
            isOpenMat = false
        ),
        ProgramEntity(
            id = "prog-tue-nogi",
            gymId = GYM_ID,
            dayIndex = 2,
            styleId = "bjj_nogi",
            title = "BJJ No-Gi — Takedowns & Passing",
            focus = "Level changes, bodylock passing and No-Gi pacing",
            ruleSetId = "adcc",
            startTime = "19:00",
            endTime = "20:30",
            level = "All levels",
            coachRole = "BJJ No-Gi Coach",
            assignedCoachId = "coach-danial",
            isOpenMat = false
        ),
        ProgramEntity(
            id = "prog-wed-judo",
            gymId = GYM_ID,
            dayIndex = 3,
            styleId = "judo",
            title = "Judo for BJJ",
            focus = "Grip fighting, breakfalls, throws into top pressure",
            ruleSetId = "none",
            startTime = "19:00",
            endTime = "20:30",
            level = "Beginner friendly",
            coachRole = "Judo Coach",
            assignedCoachId = null,
            isOpenMat = false
        ),
        ProgramEntity(
            id = "prog-thu-wrestling",
            gymId = GYM_ID,
            dayIndex = 4,
            styleId = "wrestling",
            title = "Wrestling for No-Gi BJJ",
            focus = "ADCC scoring: takedowns, guard pulls, riding and escapes",
            ruleSetId = "adcc",
            startTime = "19:00",
            endTime = "20:30",
            level = "Intermediate",
            coachRole = "Wrestling Coach",
            assignedCoachId = null,
            isOpenMat = false
        ),
        ProgramEntity(
            id = "prog-fri-striking",
            gymId = GYM_ID,
            dayIndex = 5,
            styleId = "kyokushin",
            title = "Striking for No-Gi BJJ",
            focus = "Kyokushin pressure + Muay Thai clinch, ACBJJ Battle entries",
            ruleSetId = "acbjj_battle",
            startTime = "19:00",
            endTime = "20:30",
            level = "Intermediate",
            coachRole = "Striking Coach (Kyokushin + Muay Thai)",
            assignedCoachId = null,
            isOpenMat = false
        ),
        ProgramEntity(
            id = "prog-sat-openmat",
            gymId = GYM_ID,
            dayIndex = 6,
            styleId = "bjj_gi",
            title = "BJJ Gi Open Mat",
            focus = "Free rounds, self-directed study, coach on the mat",
            ruleSetId = "ibjjf",
            startTime = "10:00",
            endTime = "12:00",
            level = "All levels",
            coachRole = "Mat supervisor",
            assignedCoachId = "coach-danial",
            isOpenMat = true
        ),
        ProgramEntity(
            id = "prog-sun-openmat",
            gymId = GYM_ID,
            dayIndex = 7,
            styleId = "bjj_nogi",
            title = "BJJ No-Gi Open Mat",
            focus = "Submission-only rounds, ADCC pacing",
            ruleSetId = "adcc",
            startTime = "10:00",
            endTime = "12:00",
            level = "All levels",
            coachRole = "Mat supervisor",
            assignedCoachId = "coach-danial",
            isOpenMat = true
        )
    )

    fun requirements() = listOf(
        RequirementEntity(
            id = "req-judo",
            gymId = GYM_ID,
            roleTitle = "Judo Coach — Wed: Judo for BJJ",
            styleId = "judo",
            ruleSetId = "none",
            requiredCredentialId = "dan_grade",
            minimumGrade = "Judo black belt (1st Dan or above)",
            minimumYears = 3,
            sessionsPerWeek = 1,
            notes = "Must be able to teach breakfalls to BJJ students and translate throws into top pressure."
        ),
        RequirementEntity(
            id = "req-wrestling",
            gymId = GYM_ID,
            roleTitle = "Wrestling Coach — Thu: No-Gi BJJ (ADCC)",
            styleId = "wrestling",
            ruleSetId = "adcc",
            requiredCredentialId = "wrestling_record",
            minimumGrade = "Competitive wrestling record (state / national / collegiate)",
            minimumYears = 3,
            sessionsPerWeek = 1,
            notes = "Programme is designed for the ADCC rule set: takedowns, riding time and escapes."
        ),
        RequirementEntity(
            id = "req-kyokushin",
            gymId = GYM_ID,
            roleTitle = "Kyokushin Coach — Fri: Striking for No-Gi BJJ",
            styleId = "kyokushin",
            ruleSetId = "acbjj_battle",
            requiredCredentialId = "dan_grade",
            minimumGrade = "Kyokushin black belt (1st Dan or above)",
            minimumYears = 3,
            sessionsPerWeek = 1,
            notes = "Shares the Friday slot with the Muay Thai coach; both must be credentialed."
        ),
        RequirementEntity(
            id = "req-muaythai",
            gymId = GYM_ID,
            roleTitle = "Muay Thai Coach — Fri: Striking for No-Gi BJJ",
            styleId = "muay_thai",
            ruleSetId = "acbjj_battle",
            requiredCredentialId = "coaching_licence",
            minimumGrade = "Certified Muay Thai coach or stadium record",
            minimumYears = 2,
            sessionsPerWeek = 1,
            notes = "Clinch and striking entries for the ACBJJ Battle Jiu-Jitsu rule set."
        )
    )

    fun coaches() = listOf(
        CoachEntity(
            id = "coach-arif",
            name = "Arif Rahman",
            headline = "Judo 3rd Dan · BJJ brown belt",
            city = "Johor Bahru",
            distanceKm = 1.4,
            hourlyRate = 150.0,
            rating = 4.8,
            reviewCount = 62,
            verified = true,
            yearsExperience = 12,
            bio = "Competed nationally in Judo before cross-training BJJ. Specialises in grip fighting and turning throws into immediate submissions.",
            styleIdsCsv = "judo,bjj_gi",
            ruleSetIdsCsv = "none,ibjjf"
        ),
        CoachEntity(
            id = "coach-danial",
            name = "Danial Hakim",
            headline = "BJJ black belt · Gi & No-Gi",
            city = "Johor Bahru",
            distanceKm = 2.1,
            hourlyRate = 120.0,
            rating = 4.9,
            reviewCount = 128,
            verified = true,
            yearsExperience = 9,
            bio = "Head coach at the academy. Structured, patient teaching with a focus on safe progression for beginners.",
            styleIdsCsv = "bjj_gi,bjj_nogi",
            ruleSetIdsCsv = "ibjjf,adcc"
        ),
        CoachEntity(
            id = "coach-rafiq",
            name = "Rafiquddin Osman",
            headline = "Wrestler · ADCC ruleset specialist",
            city = "Johor Bahru",
            distanceKm = 3.2,
            hourlyRate = 160.0,
            rating = 4.9,
            reviewCount = 74,
            verified = true,
            yearsExperience = 10,
            bio = "Former national squad wrestler. Builds No-Gi BJJ game plans around takedown chains and riding pressure.",
            styleIdsCsv = "wrestling,bjj_nogi",
            ruleSetIdsCsv = "adcc"
        ),
        CoachEntity(
            id = "coach-kenji",
            name = "Kenji Sato",
            headline = "Kyokushin 2nd Dan · Muay Thai coach",
            city = "Johor Bahru",
            distanceKm = 4.0,
            hourlyRate = 170.0,
            rating = 4.7,
            reviewCount = 41,
            verified = true,
            yearsExperience = 14,
            bio = "Kyokushin knockdown competitor turned striking coach. Teaches pressure fighting and clinch entries for grapplers.",
            styleIdsCsv = "kyokushin,muay_thai",
            ruleSetIdsCsv = "acbjj_battle"
        ),
        CoachEntity(
            id = "coach-meiling",
            name = "Mei Ling Tan",
            headline = "Muay Thai coach · ACBJJ Battle striking",
            city = "Johor Bahru",
            distanceKm = 5.1,
            hourlyRate = 95.0,
            rating = 4.8,
            reviewCount = 57,
            verified = true,
            yearsExperience = 7,
            bio = "Stadium fighter and certified coach. Focuses on teeps, knees and clinch control that transfer into No-Gi grappling.",
            styleIdsCsv = "muay_thai",
            ruleSetIdsCsv = "acbjj_battle"
        ),
        CoachEntity(
            id = "coach-hafiz",
            name = "Hafiz Zulkifli",
            headline = "Judo 1st Kyu (brown belt)",
            city = "Johor Bahru",
            distanceKm = 2.8,
            hourlyRate = 90.0,
            rating = 4.4,
            reviewCount = 19,
            verified = false,
            yearsExperience = 4,
            bio = "Assistant Judo coach working towards black belt. Strong groundwork, still building throwing repertoire.",
            styleIdsCsv = "judo",
            ruleSetIdsCsv = "none"
        ),
        CoachEntity(
            id = "coach-suresh",
            name = "Suresh Kumar",
            headline = "Freestyle wrestler · unverified record",
            city = "Johor Bahru",
            distanceKm = 6.3,
            hourlyRate = 110.0,
            rating = 4.2,
            reviewCount = 11,
            verified = false,
            yearsExperience = 5,
            bio = "Club-level wrestler with a strong folkstyle base. Competitive record is awaiting document verification.",
            styleIdsCsv = "wrestling",
            ruleSetIdsCsv = "adcc"
        )
    )

    fun credentials() = listOf(
        CredentialEntity("cred-arif-dan", "coach-arif", "dan_grade", "Judo 3rd Dan", "Malaysian Judo Association", true),
        CredentialEntity("cred-arif-ruleset", "coach-arif", "ruleset_experience", "IBJJF Gi competition", "MoMA verified", true),
        CredentialEntity("cred-arif-firstaid", "coach-arif", "first_aid", "First aid & CPR", "St John Ambulance", true),

        CredentialEntity("cred-danial-dan", "coach-danial", "dan_grade", "BJJ black belt", "IBJJF registered", true),
        CredentialEntity("cred-danial-ruleset", "coach-danial", "ruleset_experience", "ADCC & IBJJF coaching", "MoMA verified", true),
        CredentialEntity("cred-danial-safe", "coach-danial", "safeguarding", "Safeguarding in sport", "Sport Integrity Malaysia", true),

        CredentialEntity("cred-rafiq-record", "coach-rafiq", "wrestling_record", "National squad wrestler 2014–2019", "Malaysian Wrestling Federation", true),
        CredentialEntity("cred-rafiq-ruleset", "coach-rafiq", "ruleset_experience", "ADCC rule set seminars", "MoMA verified", true),
        CredentialEntity("cred-rafiq-firstaid", "coach-rafiq", "first_aid", "First aid & CPR", "Red Crescent", true),

        CredentialEntity("cred-kenji-dan", "coach-kenji", "dan_grade", "Kyokushin 2nd Dan", "IKO Kyokushinkaikan", true),
        CredentialEntity("cred-kenji-licence", "coach-kenji", "coaching_licence", "Certified striking coach", "Malaysian Muay Thai Association", true),

        CredentialEntity("cred-meiling-licence", "coach-meiling", "coaching_licence", "Certified Muay Thai coach", "Malaysian Muay Thai Association", true),
        CredentialEntity("cred-meiling-ruleset", "coach-meiling", "ruleset_experience", "ACBJJ Battle striking entries", "MoMA verified", true),

        CredentialEntity("cred-hafiz-dan", "coach-hafiz", "dan_grade", "Judo 1st Kyu (brown belt)", "Malaysian Judo Association", false),

        CredentialEntity("cred-suresh-record", "coach-suresh", "wrestling_record", "State-level freestyle record", "Unverified upload", false)
    )

    fun slots() = listOf(
        CoachSlotSeed("coach-arif", 3, "19:00"),
        CoachSlotSeed("coach-arif", 3, "20:30"),
        CoachSlotSeed("coach-arif", 6, "09:00"),
        CoachSlotSeed("coach-arif", 6, "11:00"),

        CoachSlotSeed("coach-danial", 1, "19:00"),
        CoachSlotSeed("coach-danial", 2, "19:00"),
        CoachSlotSeed("coach-danial", 6, "10:00"),
        CoachSlotSeed("coach-danial", 7, "10:00"),

        CoachSlotSeed("coach-rafiq", 4, "19:00"),
        CoachSlotSeed("coach-rafiq", 4, "20:30"),
        CoachSlotSeed("coach-rafiq", 7, "08:00"),

        CoachSlotSeed("coach-kenji", 5, "19:00"),
        CoachSlotSeed("coach-kenji", 5, "20:30"),
        CoachSlotSeed("coach-kenji", 6, "14:00"),

        CoachSlotSeed("coach-meiling", 5, "19:00"),
        CoachSlotSeed("coach-meiling", 2, "18:00"),
        CoachSlotSeed("coach-meiling", 6, "16:00"),

        CoachSlotSeed("coach-hafiz", 3, "18:00"),
        CoachSlotSeed("coach-suresh", 4, "18:00")
    ).mapIndexed { index, seed ->
        SlotEntity(
            id = "slot-${seed.coachId}-$index",
            coachId = seed.coachId,
            dayIndex = seed.dayIndex,
            time = seed.time,
            status = if (index % 5 == 3) "BOOKED" else "FREE"
        )
    }

    private data class CoachSlotSeed(val coachId: String, val dayIndex: Int, val time: String)
}
