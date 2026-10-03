package tech.salroid.filmy.ui.cast_crew

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.CastCrewDetailsResponse
import tech.salroid.filmy.data.local.model.CombinedCredit
import tech.salroid.filmy.data.local.model.CombinedCreditsResponse
import tech.salroid.filmy.data.local.model.ExternalIdsResponse
import tech.salroid.filmy.ui.common.components.ErrorWidget
import tech.salroid.filmy.ui.common.components.PreviewItem
import tech.salroid.filmy.ui.movies.details.components.ExternalLinksSection
import tech.salroid.filmy.ui.movies.details.components.FullReadSheet
import tech.salroid.filmy.ui.theme.AppTheme
import tech.salroid.filmy.utility.openUrl
import tech.salroid.filmy.utility.toReadableDate

@Composable
fun CastCrewDetailsScreen(
    memberId: Int,
    isTv: Boolean,
    viewModel: CastCrewViewModel = hiltViewModel(),
    onMovieClick: (Int, Boolean, String) -> Unit,
    onViewAllMoviesClick: (Int, Boolean, String) -> Unit,
    onBackClick: () -> Unit
) {
    val details by viewModel.uiStateCastCrewDetails.collectAsStateWithLifecycle()
    val combinedCredits by viewModel.uiStateCombinedCredits.collectAsStateWithLifecycle()
    val externalIds by viewModel.uiStateExternalIds.collectAsStateWithLifecycle()
    val isError by viewModel.uiStateError.collectAsStateWithLifecycle()

    fun load() {
        viewModel.getCastCrewDetails(memberId.toString())
        viewModel.getCombinedCredits(memberId.toString())
        viewModel.getPersonExternalIds(memberId.toString())
    }

    LaunchedEffect(memberId) {
        load()
    }

    CastCrewDetailsContent(
        details = details,
        combinedCredits = combinedCredits,
        externalIds = externalIds,
        isError = isError,
        onRetryClick = ::load,
        memberId = memberId,
        onMovieClick = onMovieClick,
        onViewAllMoviesClick = onViewAllMoviesClick,
        onBackClick = onBackClick
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun CastCrewDetailsContent(
    details: CastCrewDetailsResponse?,
    combinedCredits: CombinedCreditsResponse?,
    externalIds: ExternalIdsResponse?,
    isError: Boolean,
    onRetryClick: () -> Unit,
    memberId: Int,
    onMovieClick: (Int, Boolean, String) -> Unit,
    onViewAllMoviesClick: (Int, Boolean, String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        CastCrewDetailsBody(
            paddingValues = paddingValues,
            details = details,
            combinedCredits = combinedCredits,
            externalIds = externalIds,
            isError = isError,
            onRetryClick = onRetryClick,
            memberId = memberId,
            onMovieClick = onMovieClick,
            onViewAllMoviesClick = onViewAllMoviesClick,
            onBackClick = onBackClick
        )
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun CastCrewDetailsBody(
    paddingValues: PaddingValues,
    details: CastCrewDetailsResponse?,
    combinedCredits: CombinedCreditsResponse?,
    externalIds: ExternalIdsResponse?,
    isError: Boolean = false,
    onRetryClick: () -> Unit = {},
    memberId: Int,
    onMovieClick: (Int, Boolean, String) -> Unit,
    onViewAllMoviesClick: (Int, Boolean, String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var showFullBiography by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (details == null && isError) {
        ErrorWidget(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            message = stringResource(R.string.couldnt_load_person),
            onRetryClick = onRetryClick
        )
        return
    }

    details?.let { member ->
        val credits = remember(combinedCredits, member.name, member.knownForDepartment) {
            rankKnownFor(combinedCredits, member.name, member.knownForDepartment)
        }
        val movieCount = credits.count { !it.isTv }
        val tvCount = credits.count { it.isTv }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PersonTitleCard(member = member, creditCount = credits.size)

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
                    textAlign = TextAlign.Justify,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(
                        onClickLabel = stringResource(R.string.cd_read_full_biography),
                        role = Role.Button
                    ) { showFullBiography = true }
                )
            }

            if (showFullBiography) {
                FullReadSheet(
                    title = member.name ?: "",
                    content = member.biography ?: "",
                    onDismiss = { showFullBiography = false }
                )
            }

            if (credits.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.known_for),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                credits.take(6).chunked(3).forEach { rowCredits ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowCredits.forEach { credit ->
                            MemberCreditItem(
                                credit = credit,
                                modifier = Modifier.weight(1f),
                                onClick = { onMovieClick(credit.id ?: 0, credit.isTv, credit.displayTitle ?: "") }
                            )
                        }
                        repeat(3 - rowCredits.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (movieCount > 5 || tvCount > 5) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (movieCount > 5) {
                            TextButton(onClick = { onViewAllMoviesClick(memberId, false, member.name ?: "") }) {
                                Text(stringResource(R.string.movies) + " (" + movieCount + ")")
                            }
                        }
                        if (tvCount > 5) {
                            TextButton(onClick = { onViewAllMoviesClick(memberId, true, member.name ?: "") }) {
                                Text(stringResource(R.string.tv_shows) + " (" + tvCount + ")")
                            }
                        }
                    }
                }
            }

            ExternalLinksSection(
                homepage = member.homepage?.takeIf { it.isNotBlank() },
                imdbId = member.imdbId ?: externalIds?.imdbId,
                facebookId = externalIds?.facebookId,
                instagramId = externalIds?.instagramId,
                twitterId = externalIds?.twitterId,
                onLinkClick = { context.openUrl(it) }
            )
        }
    } ?: run {
        PersonSkeletonLoader(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
fun MemberCreditItem(credit: CombinedCredit, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(modifier = modifier) {
        PreviewItem(
            title = credit.displayTitle ?: "",
            posterUrl = "https://image.tmdb.org/t/p/w342${credit.posterPath}",
            readableDate = credit.displayDate?.toReadableDate() ?: "",
            subtitle = credit.role,
            onItemClick = onClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CastCrewDetailsScreenPreview() {
    val sampleDetails = CastCrewDetailsResponse(
        name = "Brad Pitt",
        profilePath = null,
        birthday = "1963-12-18",
        placeOfBirth = "Shawnee, Oklahoma, USA",
        knownForDepartment = "Acting",
        biography = "William Bradley Pitt is an American actor and film producer. He is the recipient of various accolades, including two Academy Awards, a British Academy Film Award, two Golden Globe Awards, and a Primetime Emmy Award."
    )
    val sampleCredits = CombinedCreditsResponse(
        cast = listOf(
            CombinedCredit(id = 1, title = "Fight Club", posterPath = null, character = "Tyler Durden", mediaType = "movie", popularity = 90.0),
            CombinedCredit(id = 2, title = "Seven", posterPath = null, character = "Detective Mills", mediaType = "movie", popularity = 80.0),
            CombinedCredit(id = 3, title = "Inglourious Basterds", posterPath = null, character = "Lt. Aldo Raine", mediaType = "movie", popularity = 75.0)
        )
    )
    AppTheme {
        CastCrewDetailsBody(
            paddingValues = PaddingValues(0.dp),
            details = sampleDetails,
            combinedCredits = sampleCredits,
            externalIds = null,
            memberId = 1,
            onMovieClick = { _, _, _ -> },
            onViewAllMoviesClick = { _, _, _ -> }
        )
    }
}
