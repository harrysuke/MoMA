package com.moma.core.model

/**
 * The six styles MoMA supports in the gym-first release. Everything else
 * (boxing, yoga, generic fitness) is explicitly out of scope until the BJJ gym
 * programme is fully covered.
 *
 * This type is deliberately free of Android/Compose dependencies so the domain
 * and its tests run on a plain JVM. Presentation colours live in
 * `com.moma.app.ui.theme.styleColor`.
 */
enum class MartialStyle(
    val id: String,
    val label: String,
    val shortLabel: String
) {
    BJJ_GI("bjj_gi", "Brazilian Jiu-Jitsu (Gi)", "BJJ Gi"),
    BJJ_NO_GI("bjj_nogi", "Brazilian Jiu-Jitsu (No-Gi)", "BJJ No-Gi"),
    JUDO("judo", "Judo", "Judo"),
    WRESTLING("wrestling", "Wrestling", "Wrestling"),
    KYOKUSHIN("kyokushin", "Kyokushin Karate", "Kyokushin"),
    MUAY_THAI("muay_thai", "Muay Thai", "Muay Thai");

    companion object {
        fun fromId(id: String): MartialStyle = entries.firstOrNull { it.id == id } ?: BJJ_GI
        fun all(): List<MartialStyle> = entries.toList()
    }
}

/** Competition rule sets that a programme is designed against. */
enum class RuleSet(val id: String, val label: String, val summary: String) {
    NONE("none", "No rule set", "Technique and hobbyist training"),
    IBJJF("ibjjf", "IBJJF", "Gi points, advantages and penalties"),
    ADCC("adcc", "ADCC", "No-Gi submission-only with overtime"),
    ACBJJ_BATTLE("acbjj_battle", "ACBJJ Battle Jiu-Jitsu", "Battle ruleset with striking entries");

    companion object {
        fun fromId(id: String?): RuleSet = entries.firstOrNull { it.id == id } ?: NONE
    }
}

/** Credential classes that can be hard-gated during matching. */
enum class CredentialType(val id: String, val label: String) {
    DAN_GRADE("dan_grade", "Dan grade certificate"),
    WRESTLING_RECORD("wrestling_record", "Competitive wrestling record"),
    COACHING_LICENCE("coaching_licence", "Coaching licence"),
    RULESET_EXPERIENCE("ruleset_experience", "Rule set experience"),
    FIRST_AID("first_aid", "First aid certificate"),
    SAFEGUARDING("safeguarding", "Safeguarding / child protection");

    companion object {
        fun fromId(id: String): CredentialType = entries.firstOrNull { it.id == id } ?: DAN_GRADE
    }
}

enum class DayOfWeek(val index: Int, val short: String, val full: String) {
    MON(1, "Mon", "Monday"),
    TUE(2, "Tue", "Tuesday"),
    WED(3, "Wed", "Wednesday"),
    THU(4, "Thu", "Thursday"),
    FRI(5, "Fri", "Friday"),
    SAT(6, "Sat", "Saturday"),
    SUN(7, "Sun", "Sunday");

    companion object {
        fun fromIndex(i: Int): DayOfWeek = entries.firstOrNull { it.index == i } ?: MON
    }
}

data class Gym(
    val id: String,
    val name: String,
    val city: String,
    val address: String,
    val tagline: String,
    val verified: Boolean
)

data class GymProgram(
    val id: String,
    val gymId: String,
    val day: DayOfWeek,
    val style: MartialStyle,
    val title: String,
    val focus: String,
    val ruleSet: RuleSet,
    val startTime: String,
    val endTime: String,
    val level: String,
    val coachRole: String,
    val assignedCoachId: String?,
    val isOpenMat: Boolean = false
)

data class CoachRequirement(
    val id: String,
    val gymId: String,
    val roleTitle: String,
    val style: MartialStyle,
    val ruleSet: RuleSet,
    val requiredCredential: CredentialType,
    val minimumGrade: String,
    val minimumYears: Int,
    val sessionsPerWeek: Int,
    val notes: String
)

data class Credential(
    val id: String,
    val coachId: String,
    val type: CredentialType,
    val label: String,
    val issuer: String,
    val verified: Boolean
)

data class Coach(
    val id: String,
    val name: String,
    val headline: String,
    val city: String,
    val distanceKm: Double,
    val hourlyRate: Double,
    val rating: Double,
    val reviewCount: Int,
    val verified: Boolean,
    val yearsExperience: Int,
    val bio: String,
    val styles: List<MartialStyle>,
    val ruleSets: List<RuleSet>,
    val credentials: List<Credential>,
    val availability: List<CoachSlot>
)

data class CoachSlot(
    val id: String,
    val day: DayOfWeek,
    val time: String,
    val status: SlotStatus
)

enum class SlotStatus { FREE, HELD, BOOKED }

enum class BookingStatus { PENDING_PAYMENT, CONFIRMED, COMPLETED, CANCELLED }

data class Booking(
    val id: String,
    val gymId: String,
    val coachId: String,
    val coachName: String,
    val programTitle: String,
    val style: MartialStyle,
    val ruleSet: RuleSet,
    val day: DayOfWeek,
    val time: String,
    val price: Double,
    val commission: Double,
    val status: BookingStatus,
    val createdAtLabel: String
) {
    val coachPayout: Double get() = price - commission
}

/** Outcome of the mandatory credential gate. */
data class GateResult(
    val passed: Boolean,
    val requirement: CoachRequirement,
    val satisfiedCredential: Credential?,
    val missing: List<String>,
    val reason: String
)

/** Explainable ranking, computed after the gate passes. */
data class MatchScore(
    val total: Int,
    val stylePoints: Int,
    val credentialPoints: Int,
    val availabilityPoints: Int,
    val distancePoints: Int,
    val ratingPoints: Int,
    val pricePoints: Int
) {
    fun breakdown(): String =
        "Style $stylePoints · Credential $credentialPoints · Availability $availabilityPoints · " +
            "Distance $distancePoints · Rating $ratingPoints · Price $pricePoints"
}

data class RankedCoach(
    val coach: Coach,
    val gate: GateResult,
    val score: MatchScore
)

data class CoverageRow(
    val requirement: CoachRequirement,
    val assignedCoach: Coach?,
    val candidateCount: Int,
    val isCovered: Boolean
)
