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
import com.moma.app.ui.components.MomaCard
import com.moma.app.ui.components.NeutralPill
import com.moma.app.ui.components.ScoreBar
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
import com.moma.app.ui.theme.Red600
import com.moma.app.ui.theme.Surface
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShortlistScreen(
    onOpenCoach: (String, String) -> Unit,
    viewModel: ShortlistViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val requirement = state.requirement

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Spacer(Modifier.height(6.dp))

        if (requirement == null) {
            EmptyState("Requirement not found", "This requirement may have been closed.")
        } else {
        Text(
            text = "Credential-gated shortlist",
            style = MaterialTheme.typography.labelMedium,
            color = Green700
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = requirement.roleTitle,
            style = MaterialTheme.typography.headlineSmall,
            color = Ink900
        )
        Spacer(Modifier.height(8.dp))
        StyleRow(styles = listOf(requirement.style))
        Spacer(Modifier.height(10.dp))

        MomaCard(background = Green50, border = Green50) {
            KeyValue("Programme day", state.programDay.full)
            KeyValue("Rule set", requirement.ruleSet.label)
            KeyValue("Hard requirement", requirement.requiredCredential.label)
            KeyValue("Minimum grade", requirement.minimumGrade)
            KeyValue("Experience", "${requirement.minimumYears}+ years")
            KeyValue("Sessions", "${requirement.sessionsPerWeek} per week")
            Spacer(Modifier.height(8.dp))
            Text(
                text = requirement.notes,
                style = MaterialTheme.typography.bodySmall,
                color = Ink500
            )
        }

        SectionHeader(title = "Ranked coaches (${state.ranked.size})")

        if (state.ranked.isEmpty()) {
            MomaCard(border = Line) {
                Text(
                    "No coach clears the credential gate yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink900
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Widen the requirement or invite a coach to upload documents. The gate is not bypassable by price or rating.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink500
                )
            }
        }

        state.ranked.forEachIndexed { index, ranked ->
            MomaCard(
                modifier = Modifier.clickable { onOpenCoach(ranked.coach.id, requirement.id) },
                border = if (index == 0) Green700.copy(alpha = 0.35f) else Line
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(ranked.coach.name, size = 42)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = ranked.coach.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = Ink900
                            )
                            Spacer(Modifier.width(6.dp))
                            if (ranked.coach.verified) SuccessPill("Verified")
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = ranked.coach.headline,
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink500
                        )
                    }
                    Text(
                        text = "#${index + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink300
                    )
                }

                Spacer(Modifier.height(12.dp))
                ScoreBar(score = ranked.score.total)

                Spacer(Modifier.height(8.dp))
                Text(
                    text = ranked.score.breakdown(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink300
                )

                Spacer(Modifier.height(10.dp))
                DividerLine()
                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuccessPill(ranked.gate.satisfiedCredential?.label ?: "Gate passed")
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = String.format(Locale.US, "RM %.0f/h · %.1f km", ranked.coach.hourlyRate, ranked.coach.distanceKm),
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink500
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "View",
                        style = MaterialTheme.typography.labelLarge,
                        color = Green700
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        if (state.rejected.isNotEmpty()) {
            SectionHeader(title = "Blocked by the gate (${state.rejected.size})")
            state.rejected.forEach { (coach, reason) ->
                MomaCard(border = Line) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(coach.name, size = 34)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = coach.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = Ink900
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = coach.headline,
                                style = MaterialTheme.typography.bodySmall,
                                color = Ink500
                            )
                        }
                        DangerPill("Blocked")
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Amber100.copy(alpha = 0.6f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(26.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Amber600)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink700
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        coach.credentials.forEach { credential ->
                            if (credential.verified) {
                                SuccessPill(credential.label)
                            } else {
                                NeutralPill("${credential.label} · pending")
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = "Gate runs first, ranking second. Rejections are shown with their reason so a hiring decision can be defended.",
            style = MaterialTheme.typography.bodySmall,
            color = Ink300
        )
        }
    }
}

@Composable
private fun KeyValue(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Ink500,
            modifier = Modifier.width(110.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink900,
            fontWeight = FontWeight.Medium
        )
    }
}
