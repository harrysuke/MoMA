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
import com.moma.app.ui.components.LabeledField
import com.moma.app.ui.components.MomaCard
import com.moma.app.ui.components.PrimaryButton
import com.moma.app.ui.components.SectionHeader
import com.moma.app.ui.components.SuccessPill
import com.moma.app.ui.theme.Green50
import com.moma.app.ui.theme.Green700
import com.moma.app.ui.theme.Ink300
import com.moma.app.ui.theme.Ink500
import com.moma.app.ui.theme.Ink700
import com.moma.app.ui.theme.Ink900
import com.moma.app.ui.theme.Line
import com.moma.app.ui.theme.Surface
import com.moma.core.model.CredentialType
import com.moma.core.model.MartialStyle
import com.moma.core.model.RuleSet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RequirementScreen(
    onDone: () -> Unit,
    viewModel: RequirementViewModel = hiltViewModel()
) {
    val form by viewModel.form.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp)
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Post a coach requirement",
            style = MaterialTheme.typography.headlineMedium,
            color = Ink900
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Requirements carry the credential gate. Coaches who cannot prove the credential are never shortlisted.",
            style = MaterialTheme.typography.bodySmall,
            color = Ink500
        )

        SectionHeader(title = "Role")
        LabeledField(
            label = "Role title",
            value = form.roleTitle,
            onValueChange = { value -> viewModel.update { it.copy(roleTitle = value) } },
            placeholder = "Judo Coach — Wed: Judo for BJJ"
        )

        Spacer(Modifier.height(14.dp))
        Text("Style", style = MaterialTheme.typography.labelMedium, color = Ink500)
        Spacer(Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MartialStyle.entries.forEach { style ->
                SelectableChip(
                    label = style.shortLabel,
                    selected = form.style == style,
                    onClick = { viewModel.update { it.copy(style = style) } }
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Text("Rule set", style = MaterialTheme.typography.labelMedium, color = Ink500)
        Spacer(Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RuleSet.entries.forEach { ruleSet ->
                SelectableChip(
                    label = ruleSet.label,
                    selected = form.ruleSet == ruleSet,
                    onClick = { viewModel.update { it.copy(ruleSet = ruleSet) } }
                )
            }
        }

        SectionHeader(title = "Credential gate")
        Text("Required credential", style = MaterialTheme.typography.labelMedium, color = Ink500)
        Spacer(Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CredentialType.entries.forEach { credential ->
                SelectableChip(
                    label = credential.label,
                    selected = form.credential == credential,
                    onClick = { viewModel.update { it.copy(credential = credential) } }
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        LabeledField(
            label = "Minimum grade / record",
            value = form.minimumGrade,
            onValueChange = { value -> viewModel.update { it.copy(minimumGrade = value) } },
            placeholder = "Black belt (1st Dan or above)"
        )

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            LabeledField(
                label = "Min. years",
                value = form.minimumYears,
                onValueChange = { value -> viewModel.update { it.copy(minimumYears = value.filter { c -> c.isDigit() }) } },
                modifier = Modifier.weight(1f)
            )
            LabeledField(
                label = "Sessions / week",
                value = form.sessionsPerWeek,
                onValueChange = { value -> viewModel.update { it.copy(sessionsPerWeek = value.filter { c -> c.isDigit() }) } },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))
        LabeledField(
            label = "Notes for candidates",
            value = form.notes,
            onValueChange = { value -> viewModel.update { it.copy(notes = value) } },
            placeholder = "Must be able to teach breakfalls to BJJ students.",
            singleLine = false
        )

        Spacer(Modifier.height(18.dp))

        if (form.submitted) {
            MomaCard(background = Green50, border = Green50) {
                SuccessPill("Published")
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${form.roleTitle.ifBlank { "Requirement" }} is now live for credentialed coaches.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink900
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Candidates must hold a verified ${form.credential.label.lowercase()} and ${form.minimumYears}+ years of coaching experience.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink700
                )
            }
            Spacer(Modifier.height(14.dp))
            PrimaryButton(text = "Back to coverage", onClick = onDone)
        } else {
            PrimaryButton(
                text = "Publish requirement",
                enabled = form.isValid,
                onClick = { viewModel.submit() }
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "The gate uses verified documents only. Unverified uploads do not count.",
                style = MaterialTheme.typography.labelSmall,
                color = Ink300
            )
        }
    }
}

@Composable
private fun SelectableChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Green700 else Surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) Surface else Ink700,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun FormDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Line)
    )
}
