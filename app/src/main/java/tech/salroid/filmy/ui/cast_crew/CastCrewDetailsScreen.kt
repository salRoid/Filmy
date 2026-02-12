package tech.salroid.filmy.ui.cast_crew

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.CastMovie
import tech.salroid.filmy.ui.theme.AppTheme
import tech.salroid.filmy.utility.toReadableDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CastCrewDetailsScreen(
    memberId: Int,
    isTv: Boolean,
    viewModel: CastCrewViewModel = hiltViewModel(),
    onMovieClick: (Int, String) -> Unit,
    onViewAllMoviesClick: (Int, Boolean, String) -> Unit,
    onBackClick: () -> Unit
) {
    val details by viewModel.uiStateCastCrewDetails.collectAsState()
    val moviesResponse by viewModel.uiStateCastCrewMovies.collectAsState()

    LaunchedEffect(memberId, isTv) {
        viewModel.getCastCrewDetails(memberId.toString())
        if (isTv) {
            viewModel.getCastCrewTvShows(memberId.toString())
        } else {
            viewModel.getCastCrewMovies(memberId.toString())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = details?.name ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        details?.let { member ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = "http://image.tmdb.org/t/p/w500${member.profilePath}",
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.default_avatar),
                    error = painterResource(R.drawable.default_avatar)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = member.name ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                member.birthday?.let {
                    Text(
                        text = it.toReadableDate(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                member.placeOfBirth?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (!member.biography.isNullOrEmpty()) {
                    Text(
                        text = stringResource(R.string.details),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = member.biography ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                val movies = moviesResponse?.castMovies ?: emptyList()
                if (movies.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isTv) stringResource(R.string.tv_shows) else stringResource(R.string.movies),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (movies.size > 5) {
                            TextButton(onClick = { onViewAllMoviesClick(memberId, isTv, member.name ?: "") }) {
                                Text(stringResource(R.string.view_all))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    movies.take(6).chunked(3).forEach { rowMovies ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowMovies.forEach { movie ->
                                MemberMovieItem(
                                    movie = movie,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onMovieClick(movie.id ?: 0, movie.title ?: "") }
                                )
                            }
                            repeat(3 - rowMovies.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun MemberMovieItem(movie: CastMovie, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "http://image.tmdb.org/t/p/w342${movie.posterPath}",
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(0.66f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.default_banner_min)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = movie.title ?: "",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CastCrewDetailsScreenPreview() {
    AppTheme {
        // Mocking behavior
    }
}
