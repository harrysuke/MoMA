package com.moma.feature.booking

import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
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
import com.moma.app.ui.components.EmptyState
import com.moma.app.ui.components.KeyValueRow
import com.moma.app.ui.components.MomaCard
import com.moma.app.ui.components.PrimaryButton
import com.moma.app.ui.components.SuccessPill
import com.moma.app.ui.theme.Amber100
import com.moma.app.ui.theme.Green50
import com.moma.app.ui.theme.Green700
import com.moma.app.ui.theme.Ink300
import com.moma.app.ui.theme.Ink500
import com.moma.app.ui.theme.Ink700
import com.moma.app.ui.theme.Ink900
import com.moma.app.ui.theme.Line
import com.moma.app.ui.theme.Surface
import java.util.Locale

@Composable
fun CheckoutScreen(
    onPaid: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.confirmed) {
        if (state.confirmed) onPaid()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp)
    ) {
        Spacer(Modifier.height(6.dp))

        if (state.loading) {
            EmptyState("Preparing checkout", "Loading the booking and the gym commission split.")
        } else if (state.coach == null) {
            EmptyState("Booking not found", "The slot may have been released after the 10 minute hold.")
        } else {
            val coach = state.coach!!
            val program = state.program

            Text(
                text = "Confirm booking",
                style = MaterialTheme.typography.headlineMedium,
                color = Ink900
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Payment confirms the slot immediately. Unpaid holds release after 10 minutes.",
                style = MaterialTheme.typography.bodySmall,
                color = Ink500
            )

            Spacer(Modifier.height(16.dp))

            MomaCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(coach.name, size = 44)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = coach.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Ink900
                        )
                        Text(
                            text = coach.headline,
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink500
                        )
                    }
                    SuccessPill("Gate passed")
                }
                Spacer(Modifier.height(12.dp))
                DividerLine()
                Spacer(Modifier.height(10.dp))
                KeyValueRow("Programme", program?.title ?: "Gym session")
                KeyValueRow("Day", program?.day?.full ?: "Scheduled day")
                KeyValueRow("Time", state.slotTime)
                KeyValueRow("Rule set", program?.ruleSet?.label ?: "—")
                KeyValueRow("Location", "Axis BJJ Academy, Johor Bahru")
            }

            Spacer(Modifier.height(14.dp))

            MomaCard(background = Green50, border = Green50) {
                Text(
                    text = "Payment split",
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink900
                )
                Spacer(Modifier.height(8.dp))
                KeyValueRow("Session price", String.format(Locale.US, "RM %.2f", state.price))
                KeyValueRow(
                    "Gym commission (15%)",
                    String.format(Locale.US, "RM %.2f", state.commission)
                )
                KeyValueRow(
                    "Coach payout",
                    String.format(Locale.US, "RM %.2f", state.coachPayout)
                )
                Spacer(Modifier.height(8.dp))
                DividerLine()
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Total today",
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink900,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = String.format(Locale.US, "RM %.2f", state.price),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Green700,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Amber100)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Ink700)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Cancellations inside 24 hours are refunded minus the platform fee.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink700
                )
            }

            Spacer(Modifier.height(18.dp))

            PrimaryButton(
                text = if (state.paying) {
                    "Processing payment…"
                } else {
                    String.format(Locale.US, "Pay RM %.2f securely", state.price)
                },
                enabled = !state.paying,
                onClick = { viewModel.pay() }
            )

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Card details are collected by the payment provider. MoMA never stores card numbers on the device.",
                style = MaterialTheme.typography.labelSmall,
                color = Ink300
            )
        }
    }
}
