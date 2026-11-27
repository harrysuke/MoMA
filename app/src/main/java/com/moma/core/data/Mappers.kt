package com.moma.core.data

import com.moma.core.database.BookingEntity
import com.moma.core.database.CoachEntity
import com.moma.core.database.CredentialEntity
import com.moma.core.database.GymEntity
import com.moma.core.database.ProgramEntity
import com.moma.core.database.RequirementEntity
import com.moma.core.database.SlotEntity
import com.moma.core.model.Booking
import com.moma.core.model.BookingStatus
import com.moma.core.model.Coach
import com.moma.core.model.CoachRequirement
import com.moma.core.model.CoachSlot
import com.moma.core.model.Credential
import com.moma.core.model.CredentialType
import com.moma.core.model.DayOfWeek
import com.moma.core.model.Gym
import com.moma.core.model.GymProgram
import com.moma.core.model.MartialStyle
import com.moma.core.model.RuleSet
import com.moma.core.model.SlotStatus

fun GymEntity.toDomain() = Gym(
    id = id,
    name = name,
    city = city,
    address = address,
    tagline = tagline,
    verified = verified
)

fun ProgramEntity.toDomain() = GymProgram(
    id = id,
    gymId = gymId,
    day = DayOfWeek.fromIndex(dayIndex),
    style = MartialStyle.fromId(styleId),
    title = title,
    focus = focus,
    ruleSet = RuleSet.fromId(ruleSetId),
    startTime = startTime,
    endTime = endTime,
    level = level,
    coachRole = coachRole,
    assignedCoachId = assignedCoachId,
    isOpenMat = isOpenMat
)

fun RequirementEntity.toDomain() = CoachRequirement(
    id = id,
    gymId = gymId,
    roleTitle = roleTitle,
    style = MartialStyle.fromId(styleId),
    ruleSet = RuleSet.fromId(ruleSetId),
    requiredCredential = CredentialType.fromId(requiredCredentialId),
    minimumGrade = minimumGrade,
    minimumYears = minimumYears,
    sessionsPerWeek = sessionsPerWeek,
    notes = notes
)

fun CredentialEntity.toDomain() = Credential(
    id = id,
    coachId = coachId,
    type = CredentialType.fromId(typeId),
    label = label,
    issuer = issuer,
    verified = verified
)

fun SlotEntity.toDomain() = CoachSlot(
    id = id,
    day = DayOfWeek.fromIndex(dayIndex),
    time = time,
    status = runCatching { SlotStatus.valueOf(status) }.getOrDefault(SlotStatus.FREE)
)

fun CoachEntity.toDomain(
    credentials: List<CredentialEntity>,
    slots: List<SlotEntity>
) = Coach(
    id = id,
    name = name,
    headline = headline,
    city = city,
    distanceKm = distanceKm,
    hourlyRate = hourlyRate,
    rating = rating,
    reviewCount = reviewCount,
    verified = verified,
    yearsExperience = yearsExperience,
    bio = bio,
    styles = styleIdsCsv.split(",").filter { it.isNotBlank() }.map { MartialStyle.fromId(it.trim()) },
    ruleSets = ruleSetIdsCsv.split(",").filter { it.isNotBlank() }.map { RuleSet.fromId(it.trim()) },
    credentials = credentials.map { it.toDomain() },
    availability = slots.map { it.toDomain() }
)

fun BookingEntity.toDomain() = Booking(
    id = id,
    gymId = gymId,
    coachId = coachId,
    coachName = coachName,
    programTitle = programTitle,
    style = MartialStyle.fromId(styleId),
    ruleSet = RuleSet.fromId(ruleSetId),
    day = DayOfWeek.fromIndex(dayIndex),
    time = time,
    price = price,
    commission = commission,
    status = runCatching { BookingStatus.valueOf(status) }.getOrDefault(BookingStatus.PENDING_PAYMENT),
    createdAtLabel = createdAtLabel
)
