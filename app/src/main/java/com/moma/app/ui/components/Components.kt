package com.moma.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.moma.app.ui.theme.Amber100
import com.moma.app.ui.theme.Amber600
import com.moma.app.ui.theme.Blue100
import com.moma.app.ui.theme.Blue600
import com.moma.app.ui.theme.Green100
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
import com.moma.app.ui.theme.color
import com.moma.core.model.MartialStyle
import java.util.Locale

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Ink900,
            modifier = Modifier.weight(1f)
        )
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction, contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text(actionLabel, style = MaterialTheme.typography.labelLarge, color = Green700)
            }
        }
    }
}

@Composable
fun MomaCard(
    modifier: Modifier = Modifier,
    border: Color = Line,
    background: Color = Surface,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        border = BorderStroke(1.dp, border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(14.dp)) { content() }
    }
}

@Composable
fun Pill(
    text: String,
    color: Color,
    background: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(
            text = text.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
fun SuccessPill(text: String) = Pill(text, Green700, Green100)

@Composable
fun WarnPill(text: String) = Pill(text, Amber600, Amber100)

@Composable
fun InfoPill(text: String) = Pill(text, Blue600, Blue100)

@Composable
fun DangerPill(text: String) = Pill(text, Red600, Red100)

@Composable
fun NeutralPill(text: String) = Pill(text, Ink500, Green50)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StyleRow(
    styles: List<MartialStyle>,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        styles.forEach { style ->
            Pill(
                text = style.shortLabel,
                color = style.color,
                background = style.color.copy(alpha = 0.12f)
            )
        }
    }
}

@Composable
fun StyleDot(style: MartialStyle, size: Int = 34) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(style.color.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = style.shortLabel.take(2),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = style.color
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Green700,
            contentColor = Surface,
            disabledContainerColor = Green100,
            disabledContentColor = Ink300
        )
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Surface,
            contentColor = Green700
        ),
        border = BorderStroke(1.dp, Line)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Ink500,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            placeholder = {
                Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = Ink300)
            },
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ScoreBar(score: Int, max: Int = 100, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$score% match",
                style = MaterialTheme.typography.labelMedium,
                color = Green700,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$score / $max",
                style = MaterialTheme.typography.labelSmall,
                color = Ink300
            )
        }
        Spacer(Modifier.height(5.dp))
        LinearProgressIndicator(
            progress = { (score.toFloat() / max.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = Green700,
            trackColor = Green100
        )
    }
}

@Composable
fun KeyValueRow(label: String, value: String, emphasise: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink500,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = if (emphasise) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = if (emphasise) Green700 else Ink700,
            fontWeight = if (emphasise) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun Avatar(name: String, size: Int = 44) {
    val initials = name.split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Green100),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            color = Green700,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DividerLine(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Line)
    )
}

@Composable
fun EmptyState(title: String, message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = Ink700)
        Spacer(Modifier.height(6.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodySmall,
            color = Ink500,
            modifier = Modifier.width(240.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}
