package com.moma.feature.profile

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
import com.moma.app.ui.components.DividerLine
import com.moma.app.ui.components.GhostButton
import com.moma.app.ui.components.MomaCard
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
import com.moma.core.datastore.UserRole

@Composable
fun ProfileScreen(
    onSignedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(state.name.ifBlank { "Axis BJJ Academy" }, size = 56)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = state.name.ifBlank { "Axis BJJ Academy" },
                    style = MaterialTheme.typography.headlineSmall,
                    color = Ink900
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.role.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink500
                    )
                    Spacer(Modifier.width(6.dp))
                    SuccessPill("Signed in")
                }
            }
        }

        SectionHeader(title = "Switch role view")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UserRole.entries.forEach { role ->
                val selected = state.role == role
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) Green700 else Surface)
                        .clickable { viewModel.switchRole(role) }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = role.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) Surface else Ink700,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Bottom navigation adapts to the selected role.",
            style = MaterialTheme.typography.labelSmall,
            color = Ink300
        )

        SectionHeader(title = "Gym")
        MomaCard {
            SettingRow("Gym profile", "Axis BJJ Academy · Johor Bahru")
            DividerLine()
            SettingRow("Weekly curriculum", "7 sessions across Gi, No-Gi, Judo, Wrestling, striking")
            DividerLine()
            SettingRow("Credential gate", "Verified documents only")
            DividerLine()
            SettingRow("Commission", "15% of each confirmed session")
        }

        SectionHeader(title = "Account")
        MomaCard {
            SettingRow("Notifications", "Booking, payment and dispute alerts")
            DividerLine()
            SettingRow("Payment methods", "Managed by the payment provider")
            DividerLine()
            SettingRow("Data & privacy", "PDPA aligned · export or delete on request")
        }

        SectionHeader(title = "About")
        MomaCard(background = Green50, border = Green50) {
            Text(
                text = "MoMA · version 0.1.0-gym-first",
                style = MaterialTheme.typography.titleMedium,
                color = Ink900
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Gym-first release: curriculum planning, credential-gated coach sourcing and commission-split booking.",
                style = MaterialTheme.typography.bodySmall,
                color = Ink500
            )
        }

        Spacer(Modifier.height(18.dp))
        GhostButton(
            text = "Sign out",
            onClick = {
                viewModel.signOut()
                onSignedOut()
            }
        )
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Ink900
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Ink300
            )
        }
    }
}
