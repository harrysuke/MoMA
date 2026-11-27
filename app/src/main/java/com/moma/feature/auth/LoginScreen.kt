package com.moma.feature.auth

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moma.app.ui.components.LabeledField
import com.moma.app.ui.components.MomaCard
import com.moma.app.ui.components.PrimaryButton
import com.moma.app.ui.theme.Green100
import com.moma.app.ui.theme.Green50
import com.moma.app.ui.theme.Green700
import com.moma.app.ui.theme.Green900
import com.moma.app.ui.theme.Ink300
import com.moma.app.ui.theme.Ink500
import com.moma.app.ui.theme.Ink700
import com.moma.app.ui.theme.Ink900
import com.moma.app.ui.theme.Line
import com.moma.app.ui.theme.Red600
import com.moma.app.ui.theme.Surface
import com.moma.core.datastore.UserRole

@Composable
fun LoginScreen(
    onSignedIn: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.signedIn) {
        if (state.signedIn) onSignedIn()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Brush.linearGradient(listOf(Green900, Green700)))
                .padding(20.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        style = MaterialTheme.typography.titleLarge,
                        color = Surface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "MoMA",
                    style = MaterialTheme.typography.displaySmall,
                    color = Surface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Marketplace of Martial Arts — gym-first release",
                    style = MaterialTheme.typography.bodySmall,
                    color = Green100
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Sign in",
            style = MaterialTheme.typography.headlineSmall,
            color = Ink900
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Gym owners hire credentialed coaches. Coaches publish availability. Students book sessions.",
            style = MaterialTheme.typography.bodySmall,
            color = Ink500
        )

        Spacer(Modifier.height(16.dp))

        Text("I am a…", style = MaterialTheme.typography.labelMedium, color = Ink500)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UserRole.entries.forEach { role ->
                val selected = state.role == role
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) Green700 else Surface)
                        .clickable { viewModel.onRole(role) }
                        .padding(vertical = 12.dp),
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

        Spacer(Modifier.height(16.dp))
        LabeledField(
            label = "Email",
            value = state.email,
            onValueChange = viewModel::onEmail,
            placeholder = "owner@axisbjj.my"
        )

        Spacer(Modifier.height(12.dp))
        LabeledField(
            label = "Display name",
            value = state.name,
            onValueChange = viewModel::onName,
            placeholder = "Axis BJJ Academy"
        )

        Spacer(Modifier.height(12.dp))
        LabeledField(
            label = "Password",
            value = state.password,
            onValueChange = viewModel::onPassword,
            placeholder = "At least 6 characters"
        )

        state.error?.let { error ->
            Spacer(Modifier.height(10.dp))
            Text(error, style = MaterialTheme.typography.bodySmall, color = Red600)
        }

        Spacer(Modifier.height(18.dp))
        PrimaryButton(
            text = "Continue",
            enabled = state.canSubmit,
            onClick = { viewModel.submit() }
        )

        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Green50)
                .clickable { viewModel.continueAsGuest() }
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Explore as Axis BJJ Academy (demo gym)",
                style = MaterialTheme.typography.labelLarge,
                color = Green700
            )
        }

        Spacer(Modifier.height(18.dp))

        MomaCard {
            Text(
                text = "Why credential gating matters",
                style = MaterialTheme.typography.titleMedium,
                color = Ink900
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "A gym advertising a Judo black belt cannot be staffed by a brown belt. MoMA checks verified documents before a coach is ever ranked.",
                style = MaterialTheme.typography.bodySmall,
                color = Ink500
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(26.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Line)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Session tokens are stored in the Android Keystore, never in plain preferences.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink300
                )
            }
        }
    }
}
