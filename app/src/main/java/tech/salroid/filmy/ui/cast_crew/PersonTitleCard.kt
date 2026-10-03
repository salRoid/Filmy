package tech.salroid.filmy.ui.cast_crew

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.CastCrewDetailsResponse
import tech.salroid.filmy.ui.common.shiftedUntil
import tech.salroid.filmy.ui.theme.AppTheme
import tech.salroid.filmy.utility.toReadableDate
import java.util.Calendar
import kotlin.math.roundToInt

// The card is always a dark screen with light billing, whatever its hue.
private const val MAX_BACKGROUND_LUMINANCE = 0.08f
private const val MIN_ACCENT_LUMINANCE = 0.45f
private const val COLOR_SHIFT_DURATION_MS = 400
private val RULE_STROKE_WIDTH = 1.dp
private val RULE_DASH_LENGTH = 4.dp
private val RULE_DASH_GAP = 4.dp

internal data class TitleCardColors(
    val background: Color,
    val text: Color,
    val accent: Color
) {
    val mutedText: Color get() = text.copy(alpha = 0.65f)

    // The backdrop deepens towards the bottom edge, like light falling off a screen.
    val backgroundEdge: Color get() = lerp(background, Color.Black, 0.35f)
}

/**
 * The fallback look, in the app's blue, for when the portrait can't supply
 * colours of its own (no photo, failed load). The scheme's primary container pair is
 * the same deep blue / pale blue in light and dark, just with the roles
 * swapped, so the darker one is always the backdrop and the lighter one the
 * text. Same idea for the accent.
 */
internal fun ColorScheme.titleCardColors() = TitleCardColors(
    background = minOf(primaryContainer, onPrimaryContainer, compareBy { it.luminance() }),
    text = maxOf(primaryContainer, onPrimaryContainer, compareBy { it.luminance() }),
    accent = maxOf(primary, inversePrimary, compareBy { it.luminance() })
)

/**
 * Colours drawn from the portrait itself, the way the details screen tints
 * itself from its banner. Null when the palette has nothing to offer.
 */
internal fun Palette.titleCardColors(): TitleCardColors? {
    val backgroundSwatch = darkMutedSwatch ?: darkVibrantSwatch ?: mutedSwatch ?: dominantSwatch
        ?: return null
    val accentSwatch = lightVibrantSwatch ?: vibrantSwatch ?: lightMutedSwatch ?: backgroundSwatch

    val accent = Color(accentSwatch.rgb).shiftedUntil(Color.White) { it.luminance() >= MIN_ACCENT_LUMINANCE }
    return TitleCardColors(
        background = Color(backgroundSwatch.rgb).shiftedUntil(Color.Black) { it.luminance() <= MAX_BACKGROUND_LUMINANCE },
        text = lerp(Color.White, accent, 0.2f),
        accent = accent
    )
}

private val CardTitleFontFamily = FontFamily(Font(R.font.barlow_black, FontWeight.Black))

internal val PersonTitleCardShape = RoundedCornerShape(24.dp)
// Widescreen, like the frame the title card would be projected on.
internal const val PersonTitleCardAspectRatio = 16f / 9f
internal val PersonTitleCardMaxWidth = 560.dp

/**
 * The person's header, laid out like a film title card: their portrait on one
 * side fading into black, and their name and vital details billed beside it.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonTitleCard(
    member: CastCrewDetailsResponse,
    creditCount: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    val fallbackColors = remember(scheme) { scheme.titleCardColors() }
    // Previews and screenshot tests render a single frame and never load the
    // portrait, so they start settled on the fallback.
    val startSettled = LocalInspectionMode.current

    // Null until the portrait has either supplied its palette or failed to
    // load. Until then the card is a plain placeholder in the theme's surface
    // colour, so it never flashes one set of colours before another.
    var resolvedColors by remember(member.profilePath, fallbackColors) {
        mutableStateOf(if (startSettled) fallbackColors else null)
    }
    val colors = resolvedColors ?: fallbackColors
    val placeholderColor = scheme.surfaceContainerHigh
    val colorShift = tween<Color>(COLOR_SHIFT_DURATION_MS)
    val backdropTop by animateColorAsState(
        if (resolvedColors == null) placeholderColor else colors.background, colorShift, label = "card_backdrop_top"
    )
    val backdropBottom by animateColorAsState(
        if (resolvedColors == null) placeholderColor else colors.backgroundEdge, colorShift, label = "card_backdrop_bottom"
    )

    // Plays once the colours are known: the portrait settles from a slight
    // zoom while the billing slides in.
    val reveal = remember(member.profilePath) { Animatable(if (startSettled) 1f else 0f) }
    val isResolved = resolvedColors != null
    LaunchedEffect(isResolved) {
        if (isResolved) {
            reveal.animateTo(1f, tween(durationMillis = 900, easing = FastOutSlowInEasing))
        }
    }

    val portraitRequest = remember(member.profilePath) {
        ImageRequest.Builder(context)
            .data("https://image.tmdb.org/t/p/w500${member.profilePath}")
            // Palette needs to read the pixels, which a hardware bitmap doesn't allow.
            .allowHardware(false)
            .build()
    }

    Row(
        modifier = modifier
            .widthIn(max = PersonTitleCardMaxWidth)
            .fillMaxWidth()
            // At least 16:9, growing taller only if the billing needs it (long
            // names, large font sizes) rather than clipping it.
            .layout { measurable, constraints ->
                val minHeight = (constraints.maxWidth / PersonTitleCardAspectRatio).roundToInt()
                val placeable = measurable.measure(constraints.copy(minHeight = minHeight))
                layout(placeable.width, placeable.height) { placeable.place(0, 0) }
            }
            .height(IntrinsicSize.Min)
            .clip(PersonTitleCardShape)
            .background(Brush.verticalGradient(listOf(backdropTop, backdropBottom)))
            .semantics(mergeDescendants = true) {}
    ) {
        Box(
            modifier = Modifier
                .weight(0.36f)
                .fillMaxHeight()
                .clipToBounds()
                // Fades the portrait itself out towards the billing, so the
                // card's backdrop shows through without a seam.
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.horizontalGradient(0.5f to Color.Black, 1f to Color.Transparent),
                        blendMode = BlendMode.DstIn
                    )
                }
        ) {
            AsyncImage(
                model = portraitRequest,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val scale = 1.12f - 0.12f * reveal.value
                        scaleX = scale
                        scaleY = scale
                        alpha = reveal.value
                    },
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                error = painterResource(R.drawable.default_avatar),
                onError = { resolvedColors = fallbackColors },
                onSuccess = { imageState ->
                    val drawable = imageState.result.image.asDrawable(context.resources)
                    if (drawable is BitmapDrawable) {
                        Palette.from(drawable.bitmap).generate { palette ->
                            resolvedColors = palette?.titleCardColors() ?: fallbackColors
                        }
                    } else {
                        resolvedColors = fallbackColors
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .weight(0.64f)
                .fillMaxHeight()
                .padding(start = 8.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
                .graphicsLayer {
                    alpha = reveal.value
                    translationX = (1f - reveal.value) * 24.dp.toPx()
                },
            verticalArrangement = Arrangement.Center
        ) {
            member.knownForDepartment?.takeIf { it.isNotBlank() }?.let { department ->
                Text(
                    text = department.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 3.sp,
                    color = colors.accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = member.name.orEmpty().uppercase(),
                fontFamily = CardTitleFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                lineHeight = 26.sp,
                letterSpacing = 1.sp,
                color = colors.text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))
            val ruleColor = colors.accent.copy(alpha = 0.25f)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(RULE_STROKE_WIDTH)
            ) {
                drawLine(
                    color = ruleColor,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = size.height,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(RULE_DASH_LENGTH.toPx(), RULE_DASH_GAP.toPx())
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            val birthday = member.birthday?.takeIf { it.isNotBlank() }
            val deathday = member.deathday?.takeIf { it.isNotBlank() }
            val age = remember(birthday, deathday) { ageInYears(birthday, deathday) }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                birthday?.let {
                    BillingFact(stringResource(R.string.person_born), it.toReadableDate(), colors)
                }
                deathday?.let {
                    BillingFact(stringResource(R.string.person_died), it.toReadableDate(), colors)
                }
                age?.let {
                    BillingFact(stringResource(R.string.person_age), it.toString(), colors)
                }
                if (creditCount > 0) {
                    BillingFact(stringResource(R.string.person_credits), creditCount.toString(), colors)
                }
            }

            member.placeOfBirth?.takeIf { it.isNotBlank() }?.let { place ->
                Spacer(modifier = Modifier.height(6.dp))
                BillingFact(stringResource(R.string.person_birthplace), place, colors)
            }
        }
    }
}

@Composable
private fun BillingFact(label: String, value: String, colors: TitleCardColors) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            letterSpacing = 1.5.sp,
            color = colors.mutedText,
            maxLines = 1
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
            color = colors.text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Whole years between [birthday] and [deathday] (or [today] while alive), both
 * "yyyy-MM-dd". Null when the birthday is missing or malformed.
 */
internal fun ageInYears(
    birthday: String?,
    deathday: String?,
    today: Calendar = Calendar.getInstance()
): Int? {
    val (birthYear, birthMonth, birthDay) = birthday.toYearMonthDay() ?: return null
    val (endYear, endMonth, endDay) = deathday.toYearMonthDay()
        ?: Triple(today[Calendar.YEAR], today[Calendar.MONTH] + 1, today[Calendar.DAY_OF_MONTH])

    val hadBirthdayThisYear = endMonth > birthMonth || (endMonth == birthMonth && endDay >= birthDay)
    val age = endYear - birthYear - if (hadBirthdayThisYear) 0 else 1
    return age.takeIf { it >= 0 }
}

private fun String?.toYearMonthDay(): Triple<Int, Int, Int>? {
    val parts = this?.split("-")?.mapNotNull { it.toIntOrNull() } ?: return null
    return if (parts.size == 3) Triple(parts[0], parts[1], parts[2]) else null
}

@Preview
@Composable
private fun PersonTitleCardPreview() {
    AppTheme {
        PersonTitleCard(
            member = CastCrewDetailsResponse(
                name = "Brad Pitt",
                knownForDepartment = "Acting",
                birthday = "1963-12-18",
                placeOfBirth = "Shawnee, Oklahoma, USA"
            ),
            creditCount = 87
        )
    }
}
