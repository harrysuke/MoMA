package com.moma.feature.gym

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moma.app.ui.components.Avatar
import com.moma.app.ui.components.EmptyState
import com.moma.app.ui.components.GhostButton
import com.moma.app.ui.components.MomaCard
import com.moma.app.ui.components.NeutralPill
import com.moma.app.ui.components.SectionHeader
import com.moma.app.ui.components.StyleDot
import com.moma.app.ui.components.SuccessPill
import com.moma.app.ui.components.WarnPill
import com.moma.app.ui.theme.Green100
import com.moma.app.ui.theme.Green50
import com.moma.app.ui.theme.Green700
import com.moma.app.ui.theme.Green900
import com.moma.app.ui.theme.Ink300
import com.moma.app.ui.theme.Ink500
import com.moma.app.ui.theme.Ink700
import com.moma.app.ui.theme.Ink900
import com.moma.app.ui.theme.Line
import com.moma.app.ui.theme.Surface
import com.moma.core.model.CoverageRow
import com.moma.core.model.GymProgram

@Composable
fun GymHomeScreen(
    onOpenShortlist: (String) -> Unit,
    onOpenCurriculum: () -> Unit,
    onOpenRequirement: () -> Unit,
    viewModel: GymHomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        // Gym identity header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(Green900, Green700)))
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.gym?.name ?: "Axis BJJ Academy",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Surface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    if (state.gym?.verified == true) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Surface.copy(alpha = 0.18f))
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "VERIFIED",
                                style = MaterialTheme.typography.labelSmall,
                                color = Surface
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = state.gym?.tagline ?: "BJJ-first academy",
                    style = MaterialTheme.typography.bodySmall,
                    color = Green100
                )
                Spacer(Modifier.height(14.dp))
                Row {
                    HeaderStat("${state.assignedCount}/4", "roles staffed")
                    Spacer(Modifier.width(22.dp))
                    HeaderStat("${state.programs.size}", "weekly sessions")
                    Spacer(Modifier.width(22.dp))
                    HeaderStat("${state.openRoles}", "open")
                }
            }
        }

        SectionHeader(
            title = "Today · ${state.today.full}",
            actionLabel = "Full week",
            onAction = onOpenCurriculum
        )

        if (state.todayPrograms.isEmpty()) {
            MomaCard(background = Green50, border = Green100) {
                Text(
                    "No session scheduled today",
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink700
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Open mats run on Saturday and Sunday at 10:00.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink500
                )
            }
        } else {
            state.todayPrograms.forEach { program ->
                ProgramRow(program = program, coachName = coachNameFor(program, state.coverage))
                Spacer(Modifier.height(8.dp))
            }
        }

        SectionHeader(title = "Specialist coverage")

        if (state.loading) {
            EmptyState("Loading coverage", "Reading the curriculum and coach credentials from the local database.")
        } else {
            state.coverage.forEach { row ->
                CoverageCard(row = row, onOpen = { onOpenShortlist(row.requirement.id) })
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(6.dp))
        GhostButton(text = "Post a new coach requirement", onClick = onOpenRequirement)
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Matching is credential-gated: a coach without a verified dan grade, wrestling record or coaching licence never reaches the shortlist.",
            style = MaterialTheme.typography.bodySmall,
            color = Ink300
        )
    }
}

@Composable
private fun HeaderStat(value: String, label: String) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = Surface,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Green100
        )
    }
}

private fun coachNameFor(program: GymProgram, coverage: List<CoverageRow>): String? {
    val id = program.assignedCoachId ?: return null
    return coverage.flatMap { listOfNotNull(it.assignedCoach) }
        .firstOrNull { it.id == id }?.name
}

@Composable
private fun ProgramRow(program: GymProgram, coachName: String?) {
    MomaCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StyleDot(program.style)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = program.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink900
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${program.startTime}–${program.endTime} · ${program.level}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink500
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (program.isOpenMat) {
                        NeutralPill("Open mat")
                    } else {
                        SuccessPill(program.ruleSet.label)
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = coachName ?: program.coachRole,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (coachName == null) Ink300 else Ink500
                    )
                }
            }
        }
    }
}

@Composable
private fun CoverageCard(row: CoverageRow, onOpen: () -> Unit) {
    val covered = row.assignedCoach != null
    MomaCard(
        modifier = Modifier.clickable(onClick = onOpen),
        border = if (covered) Green100 else Line
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StyleDot(row.requirement.style, size = 38)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = row.requirement.roleTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink900
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = row.requirement.minimumGrade,
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink500
                )
            }
            Spacer(Modifier.width(8.dp))
            if (covered) SuccessPill("Staffed") else WarnPill("Open")
        }

        Spacer(Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (row.assignedCoach != null) {
                Avatar(row.assignedCoach.name, size = 30)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = row.assignedCoach.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink700,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Green50),
                    contentAlignment = Alignment.Center
                ) {
                    Text("?", style = MaterialTheme.typography.labelMedium, color = Ink300)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${row.candidateCount} credentialed candidate${if (row.candidateCount == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (row.candidateCount > 0) Ink700 else Ink300,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = "Shortlist",
                style = MaterialTheme.typography.labelLarge,
                color = Green700,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onOpen)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(Line)
    )
}

@Composable
private fun ArrangementSpacer() {
    Spacer(Modifier.width(12.dp))
}
