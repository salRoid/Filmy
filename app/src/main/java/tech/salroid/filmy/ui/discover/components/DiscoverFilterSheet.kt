package tech.salroid.filmy.ui.discover.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Calendar
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.Genre
import tech.salroid.filmy.data.local.model.discover.DiscoverFilters
import tech.salroid.filmy.data.local.model.discover.DiscoverSort
import tech.salroid.filmy.ui.common.components.CategorySelector

private fun labelFor(sort: DiscoverSort): Int = when (sort) {
    DiscoverSort.POPULARITY_DESC -> R.string.discover_sort_popularity
    DiscoverSort.VOTE_AVERAGE_DESC -> R.string.discover_sort_rating
    DiscoverSort.RELEASE_DATE_DESC -> R.string.discover_sort_newest
    DiscoverSort.RELEASE_DATE_ASC -> R.string.discover_sort_oldest
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverFilterSheet(
    initialFilters: DiscoverFilters,
    genres: List<Genre>,
    onApply: (DiscoverFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var draftGenreIds by remember { mutableStateOf(initialFilters.genreIds) }
    var draftYear by remember { mutableStateOf(initialFilters.year) }
    var draftMinRating by remember { mutableStateOf(initialFilters.minRating ?: 0f) }
    var draftSortBy by remember { mutableStateOf(initialFilters.sortBy) }
    var draftKeywordId by remember { mutableStateOf(initialFilters.keywordId) }
    var draftKeywordName by remember { mutableStateOf(initialFilters.keywordName) }

    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val yearOptions = remember { listOf(null) + (0..15).map { currentYear - it } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.discover_filters),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (draftKeywordId != null && draftKeywordName != null) {
                FilterChip(
                    selected = true,
                    onClick = {
                        draftKeywordId = null
                        draftKeywordName = null
                    },
                    label = { Text(draftKeywordName ?: "") },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (genres.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.discover_genre_label),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    genres.forEach { genre ->
                        val id = genre.id ?: return@forEach
                        val selected = id in draftGenreIds
                        FilterChip(
                            selected = selected,
                            onClick = {
                                draftGenreIds = if (selected) {
                                    draftGenreIds - id
                                } else {
                                    draftGenreIds + id
                                }
                            },
                            label = { Text(genre.name ?: "") }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.discover_year_label),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    CategorySelector(
                        categories = yearOptions,
                        selected = draftYear,
                        label = { it?.toString() ?: stringResource(R.string.discover_any_year) },
                        onSelected = { draftYear = it }
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.discover_sort_label),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    CategorySelector(
                        categories = DiscoverSort.entries,
                        selected = draftSortBy,
                        label = { stringResource(labelFor(it)) },
                        onSelected = { draftSortBy = it }
                    )
                }
            }

            Text(
                text = stringResource(
                    R.string.discover_min_rating_label
                ) + ": " + String.format("%.1f", draftMinRating),
                style = MaterialTheme.typography.titleMedium
            )
            Slider(
                value = draftMinRating,
                onValueChange = { draftMinRating = it },
                valueRange = 0f..10f,
                steps = 19,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        draftGenreIds = emptySet()
                        draftYear = null
                        draftMinRating = 0f
                        draftSortBy = DiscoverSort.POPULARITY_DESC
                        draftKeywordId = null
                        draftKeywordName = null
                    }
                ) {
                    Text(stringResource(R.string.discover_reset))
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onApply(
                            DiscoverFilters(
                                genreIds = draftGenreIds,
                                year = draftYear,
                                minRating = draftMinRating.takeIf { it > 0f },
                                sortBy = draftSortBy,
                                keywordId = draftKeywordId,
                                keywordName = draftKeywordName
                            )
                        )
                    }
                ) {
                    Text(stringResource(R.string.discover_apply))
                }
            }
        }
    }
}
