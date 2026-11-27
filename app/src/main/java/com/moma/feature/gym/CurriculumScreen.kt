package com.moma.feature.gym

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moma.app.ui.components.DividerLine
import com.moma.app.ui.components.MomaCard
import com.moma.app.ui.components.NeutralPill
import com.moma.app.ui.components.StyleDot
import com.moma.app.ui.components.SuccessPill
import com.moma.app.ui.components.WarnPill
import com.moma.app.ui.theme.Green100
import com.moma.app.ui.theme.Green50
import com.moma.app.ui.theme.Green700
import com.moma.app.ui.theme.Ink300
import com.moma.app.ui.theme.Ink500
import com.moma.app.ui.theme.Ink700
import com.moma.app.ui.theme.Ink900
import com.moma.app.ui.theme.Line
import com.moma.app.ui.theme.Surface
import com.moma.app.ui.theme.color
import com.moma.core.model.DayOfWeek

@Composable
fun CurriculumScreen(
    onOpenShortlist: (String) -> Unit,
    viewModel: CurriculumViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedDay by remember { mutableStateOf(DayOfWeek.MON) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Weekly curriculum",
            style = MaterialTheme.typography.headlineMedium,
            color = Ink900
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Gi and No-Gi BJJ, Judo, Wrestling and striking — one programme per day.",
            style = MaterialTheme.typography.bodySmall,
            color = Ink500
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DayOfWeek.entries.forEach { day ->
                val selected = day == selectedDay
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) Green700 else Surface)
                        .clickable { selectedDay = day }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = day.short,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) Surface else Ink700,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val section = state.days.firstOrNull { it.day == selectedDay }

        if (section == null || section.programs.isEmpty()) {
            MomaCard {
                Text("Nothing scheduled", style = MaterialTheme.typography.titleMedium, color = Ink700)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Add a programme for ${selectedDay.full} to publish it to the gym calendar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink500
                )
            }
        } else {
            section.programs.forEach { program ->
                MomaCard(border = program.style.color.copy(alpha = 0.25f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StyleDot(program.style, size = 40)
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
                        }
                        if (program.isOpenMat) NeutralPill("Open mat") else SuccessPill(program.ruleSet.label)
                    }

                    Spacer(Modifier.height(10.dp))
                    DividerLine()
                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = program.focus,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink700
                    )

                    Spacer(Modifier.height(10.dp))

                    val coachName = program.assignedCoachId?.let { section.coachNames[it] }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "COACH",
                                style = MaterialTheme.typography.labelSmall,
                                color = Ink300
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = coachName ?: program.coachRole,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (coachName == null) Ink500 else Ink900,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (coachName == null) {
                            WarnPill("Unstaffed")
                        } else {
                            SuccessPill("Confirmed")
                        }
                    }

                    if (!program.isOpenMat) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Find credentialed coach",
                            style = MaterialTheme.typography.labelLarge,
                            color = Green700,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Green50)
                                .clickable {
                                    val requirementId = when (program.style.id) {
                                        "judo" -> "req-judo"
                                        "wrestling" -> "req-wrestling"
                                        "kyokushin" -> "req-kyokushin"
                                        "muay_thai" -> "req-muaythai"
                                        else -> "req-judo"
                                    }
                                    onOpenShortlist(requirementId)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Green50)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Green100)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Friday striking is co-taught: Kyokushin pressure work and Muay Thai clinch entries, both mapped to the ACBJJ Battle Jiu-Jitsu rule set.",
                style = MaterialTheme.typography.bodySmall,
                color = Ink500
            )
        }
    }
}
