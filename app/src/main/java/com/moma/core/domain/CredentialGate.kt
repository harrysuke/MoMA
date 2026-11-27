package com.moma.core.domain

import com.moma.core.model.Coach
import com.moma.core.model.CoachRequirement
import com.moma.core.model.Credential
import com.moma.core.model.CredentialType
import com.moma.core.model.GateResult
import com.moma.core.model.RuleSet

/**
 * The credential gate is a **hard filter**, not a score adjustment.
 *
 * A gym cannot advertise "Judo black belt" and then book a brown belt because
 * the ranking liked their price. So the gate runs first and returns an explicit,
 * auditable reason when it rejects a coach. Only coaches that pass the gate are
 * ranked.
 */
object CredentialGate {

    fun evaluate(requirement: CoachRequirement, coach: Coach): GateResult {
        val styleOk = coach.styles.contains(requirement.style)
        if (!styleOk) {
            return GateResult(
                passed = false,
                requirement = requirement,
                satisfiedCredential = null,
                missing = listOf("Style: ${requirement.style.label}"),
                reason = "Teaches a different style — cannot cover ${requirement.style.label}."
            )
        }

        val matching = coach.credentials.filter { it.type == requirement.requiredCredential }
        val verified = matching.firstOrNull { it.verified }

        if (verified == null) {
            val missing = buildList {
                add("${requirement.requiredCredential.label} (verified)")
                if (matching.isNotEmpty()) add("Document verification pending")
            }
            return GateResult(
                passed = false,
                requirement = requirement,
                satisfiedCredential = matching.firstOrNull(),
                missing = missing,
                reason = if (matching.isEmpty()) {
                    "No ${requirement.requiredCredential.label.lowercase()} on file."
                } else {
                    "${requirement.requiredCredential.label} uploaded but not verified."
                }
            )
        }

        if (coach.yearsExperience < requirement.minimumYears) {
            return GateResult(
                passed = false,
                requirement = requirement,
                satisfiedCredential = verified,
                missing = listOf("${requirement.minimumYears}+ years coaching experience"),
                reason = "Only ${coach.yearsExperience} years of coaching experience; the programme needs ${requirement.minimumYears}+."
            )
        }

        if (requirement.ruleSet != RuleSet.NONE && !coach.ruleSets.contains(requirement.ruleSet)) {
            val rulesetCredential = coach.credentials.firstOrNull {
                it.type == CredentialType.RULESET_EXPERIENCE && it.verified
            }
            if (rulesetCredential == null) {
                return GateResult(
                    passed = false,
                    requirement = requirement,
                    satisfiedCredential = verified,
                    missing = listOf("${requirement.ruleSet.label} rule set experience"),
                    reason = "No verified ${requirement.ruleSet.label} rule set experience."
                )
            }
        }

        if (!coach.verified) {
            return GateResult(
                passed = false,
                requirement = requirement,
                satisfiedCredential = verified,
                missing = listOf("Identity verification"),
                reason = "Coach identity is not verified yet."
            )
        }

        return GateResult(
            passed = true,
            requirement = requirement,
            satisfiedCredential = verified,
            missing = emptyList(),
            reason = "Meets ${requirement.minimumGrade} with verified ${verified.label}."
        )
    }

    /** Convenience for UI: which credential satisfied the gate, if any. */
    fun satisfiedLabel(result: GateResult): String =
        result.satisfiedCredential?.label ?: "Not satisfied"

    fun credentialSummary(credentials: List<Credential>): String =
        credentials.joinToString(" · ") { it.label }
}
