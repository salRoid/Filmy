package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.common.model.PaletteColors
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun CollectionTeaserRow(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    paletteColors: PaletteColors? = null
) {
    val backgroundColor = rememberPaletteCardColor(
        paletteColors = paletteColors,
        fallback = MaterialTheme.colorScheme.secondaryContainer
    )
    val contentColor = if (paletteColors != null) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            ),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 0.5.dp,
            color = backgroundColor.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Part of a Collection",
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.7f)
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = contentColor
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = contentColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CollectionTeaserRowPreview() {
    AppTheme {
        CollectionTeaserRow(
            name = "The Dark Knight Collection",
            onClick = {}
        )
    }
}
