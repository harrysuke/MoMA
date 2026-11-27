package com.moma.feature.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moma.app.ui.components.DividerLine
import com.moma.app.ui.components.EmptyState
import com.moma.app.ui.components.MomaCard
import com.moma.app.ui.components.NeutralPill
import com.moma.app.ui.components.SectionHeader
import com.moma.app.ui.components.StyleDot
import com.moma.app.ui.components.SuccessPill
import com.moma.app.ui.components.WarnPill
import com.moma.app.ui.theme.Green50
import com.moma.app.ui.theme.Green700
import com.moma.app.ui.theme.Ink300
import com.moma.app.ui.theme.Ink500
import com.moma.app.ui.theme.Ink700
import com.moma.app.ui.theme.Ink900
import com.moma.app.ui.theme.Line
import com.moma.core.model.Booking
import com.moma.core.model.BookingStatus
import java.util.Locale

@Composable
fun BookingsScreen(
    onFindCoach: () -> Unit,
    viewModel: BookingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp)
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Bookings",
            style = MaterialTheme.typography.headlineMedium,
            color = Ink900
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Every confirmed session carries its rule set and commission split.",
            style = MaterialTheme.typography.bodySmall,
            color = Ink500
        )

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                value = String.format(Locale.US, "RM %.0f", state.totalSpend),
                label = "committed",
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = String.format(Locale.US, "RM %.0f", state.totalCommission),
                label = "commission",
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = state.upcoming.size.toString(),
                label = "active",
                modifier = Modifier.weight(1f)
            )
        }

        SectionHeader(title = "Active")

        if (state.upcoming.isEmpty()) {
            EmptyState(
                "No bookings yet",
                "Pick a credentialed coach from the shortlist to schedule the first session."
            )
            TextButton(onClick = onFindCoach) {
                Text("Go to coverage", color = Green700)
            }
        } else {
            state.upcoming.forEach { booking ->
                BookingCard(
                    booking = booking,
                    onCancel = { viewModel.cancel(booking.id) },
                    onComplete = { viewModel.complete(booking.id) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }

        if (state.past.isNotEmpty()) {
            SectionHeader(title = "Cancelled")
            state.past.forEach { booking ->
                MomaCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StyleDot(booking.style)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = booking.programTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = Ink900
                            )
                            Text(
                                text = "${booking.coachName} · ${booking.day.short} ${booking.time}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Ink500
                            )
                        }
                        NeutralPill("Cancelled")
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun StatTile(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Green50)
            .padding(vertical = 12.dp, horizontal = 10.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = Green700,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Ink500
        )
    }
}

@Composable
private fun BookingCard(
    booking: Booking,
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    MomaCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StyleDot(booking.style)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = booking.programTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink900
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${booking.coachName} · ${booking.day.full} ${booking.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink500
                )
            }
            when (booking.status) {
                BookingStatus.CONFIRMED -> SuccessPill("Confirmed")
                BookingStatus.PENDING_PAYMENT -> WarnPill("Unpaid")
                BookingStatus.COMPLETED -> NeutralPill("Completed")
                BookingStatus.CANCELLED -> NeutralPill("Cancelled")
            }
        }

        Spacer(Modifier.height(10.dp))
        DividerLine()
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = String.format(Locale.US, "RM %.2f", booking.price),
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink900
                )
                Text(
                    text = String.format(
                        Locale.US,
                        "Commission RM %.2f · payout RM %.2f",
                        booking.commission,
                        booking.coachPayout
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink300
                )
            }
            TextButton(onClick = onCancel) {
                Text("Cancel", color = Ink500, style = MaterialTheme.typography.labelMedium)
            }
            TextButton(onClick = onComplete) {
                Text("Mark done", color = Green700, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
