package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.Cast
import tech.salroid.filmy.data.local.model.Crew
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun MemberItem(
    name: String?,
    description: String?,
    profilePath: String?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = stringResource(R.string.member_profile_url, profilePath ?: ""),
                contentDescription = null,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.default_avatar),
                error = painterResource(R.drawable.default_avatar)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                )
                if (!description.isNullOrEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 70.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun SeeAllItem(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(28.dp))
            Text(
                text = stringResource(R.string.view_all),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CastSection(
    cast: List<Cast>?,
    id: Int,
    title: String,
    isTv: Boolean,
    onViewAllCastClick: (Int, Boolean, String) -> Unit,
    onMemberClick: (Int, Boolean) -> Unit
) {
    if (cast?.isNotEmpty() == true) {
        DetailsSection(
            title = stringResource(R.string.cast)
        ) {
            Column {
                cast.take(4).forEach { castMember ->
                    MemberItem(
                        name = castMember.name,
                        description = castMember.character,
                        profilePath = castMember.profilePath,
                        onClick = { onMemberClick(castMember.id ?: 0, isTv) }
                    )
                }
                if (cast.size > 4) {
                    SeeAllItem(onClick = { onViewAllCastClick(id, isTv, title) })
                }
            }
        }
    }
}

@Composable
fun CrewSection(
    crew: List<Crew>?,
    id: Int,
    title: String,
    isTv: Boolean,
    onViewAllCastClick: (Int, Boolean, String) -> Unit,
    onMemberClick: (Int, Boolean) -> Unit
) {
    if (crew?.isNotEmpty() == true) {
        DetailsSection(
            title = stringResource(R.string.crew)
        ) {
            Column {
                crew.take(4).forEach { crewMember ->
                    MemberItem(
                        name = crewMember.name,
                        description = crewMember.job,
                        profilePath = crewMember.profilePath,
                        onClick = { onMemberClick(crewMember.id ?: 0, isTv) }
                    )
                }
                if (crew.size > 4) {
                    SeeAllItem(onClick = { onViewAllCastClick(id, isTv, title) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CastSectionPreview() {
    val sampleCast = listOf(
        Cast(id = 1, name = "Dave Bautista", character = "Eric", profilePath = null),
        Cast(id = 2, name = "John Cena", character = "Peacemaker", profilePath = null),
        Cast(id = 3, name = "The Rock", character = "Black Adam", profilePath = null),
        Cast(id = 4, name = "Jason Statham", character = "Deckard Shaw", profilePath = null),
        Cast(id = 5, name = "Vin Diesel", character = "Dominic Toretto", profilePath = null)
    )
    AppTheme {
        CastSection(
            cast = sampleCast,
            id = 1,
            title = "Inception",
            isTv = false,
            onViewAllCastClick = { _, _, _ -> },
            onMemberClick = { _, _ -> }
        )
    }
}
