package com.moma.feature.gym

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moma.app.ui.components.Avatar
import com.moma.app.ui.components.DangerPill
import com.moma.app.ui.components.DividerLine
import com.moma.app.ui.components.EmptyState
import com.moma.app.ui.components.KeyValueRow
import com.moma.app.ui.components.MomaCard
import com.moma.app.ui.components.NeutralPill
import com.moma.app.ui.components.PrimaryButton
import com.moma.app.ui.components.SectionHeader
import com.moma.app.ui.components.StyleRow
import com.moma.app.ui.components.SuccessPill
import com.moma.app.ui.theme.Amber100
import com.moma.app.ui.theme.Amber600
import com.moma.app.ui.theme.Green50
import com.moma.app.ui.theme.Green700
import com.moma.app.ui.theme.Ink300
import com.moma.app.ui.theme.Ink500
import com.moma.app.ui.theme.Ink700
import com.moma.app.ui.theme.Ink900
import com.moma.app.ui.theme.Line
import com.moma.app.ui.theme.Red100
import com.moma.app.ui.theme.Red600
import com.moma.app.ui.theme.Surface
import com.moma.core.model.SlotStatus
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CoachDetailScreen(
    onBook: (bookingId: String, coachId: String, programId: String) -> Unit,
    viewModel: CoachDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val coach = state.coach
    val requirement = state.requirement

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp)
    ) {
        Spacer(Modifier.height(6.dp))

        if (coach == null) {
            EmptyState("Coach not found", "The profile may have been removed.")
        } else {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(coach.name, size = 56)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = coach.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Ink900
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = coach.headline,
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink500
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (coach.verified) SuccessPill("Identity verified") else NeutralPill("Unverified")
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = String.format(Locale.US, "★ %.1f (%d)", coach.rating, coach.reviewCount),
                        style = MaterialTheme.typography.labelMedium,
                        color = Ink700
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        StyleRow(styles = coach.styles)

        Spacer(Modifier.height(14.dp))

        // Gate banner
        val gatePassed = state.gatePassed
        if (gatePassed != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (gatePassed) Green50 else Red100)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = if (gatePassed) "Credential gate passed" else "Credential gate failed",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (gatePassed) Green700 else Red600
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = state.gateReason,
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink700
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        MomaCard {
            KeyValueRow("Hourly rate", String.format(Locale.US, "RM %.2f", coach.hourlyRate), emphasise = true)
            KeyValueRow("Distance", String.format(Locale.US, "%.1f km from the gym", coach.distanceKm))
            KeyValueRow("Experience", "${coach.yearsExperience} years coaching")
            KeyValueRow("Rule sets", coach.ruleSets.joinToString(", ") { it.label })
        }

        SectionHeader(title = "Bio")
        Text(
            text = coach.bio,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink700
        )

        SectionHeader(title = "Credentials")
        coach.credentials.forEach { credential ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (credential.verified) Green700 else Amber600)
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = credential.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink900,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = credential.issuer,
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink300
                    )
                }
                if (credential.verified) SuccessPill("Verified") else NeutralPill("Pending")
            }
        }

        if (requirement != null) {
            SectionHeader(title = "Availability for ${requirement.roleTitle.substringBefore("—").trim()}")
        } else {
            SectionHeader(title = "Availability")
        }

        if (coach.availability.isEmpty()) {
            Text(
                "No slots published yet.",
                style = MaterialTheme.typography.bodySmall,
                color = Ink300
            )
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                coach.availability.forEach { slot ->
                    val selected = state.selectedSlot == slot.id
                    val enabled = slot.status == SlotStatus.FREE
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when {
                                    selected -> Green700
                                    slot.status == SlotStatus.HELD -> Amber100
                                    !enabled -> Surface
                                    else -> Green50
                                }
                            )
                            .clickable(enabled = enabled) { viewModel.selectSlot(slot.id) }
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Column {
                            Text(
                                text = slot.day.short,
                                style = MaterialTheme.typography.labelSmall,
                                color = when {
                                    selected -> Surface.copy(alpha = 0.8f)
                                    enabled -> Ink500
                                    else -> Ink300
                                }
                            )
                            Text(
                                text = slot.time,
                                style = MaterialTheme.typography.labelLarge,
                                color = when {
                                    selected -> Surface
                                    enabled -> Ink900
                                    else -> Ink300
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Held slots release automatically after 10 minutes if payment is not completed.",
                style = MaterialTheme.typography.labelSmall,
                color = Ink300
            )
        }

        state.bookingMessage?.let { message ->
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Amber100)
                    .padding(10.dp)
            ) {
                Text(message, style = MaterialTheme.typography.bodySmall, color = Ink700)
            }
        }

        Spacer(Modifier.height(18.dp))

        PrimaryButton(
            text = if (state.gatePassed == true) {
                String.format(Locale.US, "Request booking · RM %.2f", coach.hourlyRate)
            } else {
                "Coach is not eligible for this role"
            },
            enabled = state.gatePassed == true && state.selectedSlot != null,
            onClick = {
                viewModel.requestBooking { bookingId ->
                    onBook(bookingId, coach.id, "prog-" + (requirement?.style?.id ?: "judo"))
                }
            }
        )

        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            DangerPill("Policy")
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Bookings are confirmed only after payment. The gym keeps 15% commission on each session.",
                style = MaterialTheme.typography.labelSmall,
                color = Ink300
            )
        }

        DividerLine(Modifier.padding(top = 16.dp))
        }
    }
}
