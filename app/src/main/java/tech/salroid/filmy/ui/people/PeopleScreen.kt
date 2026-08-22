package tech.salroid.filmy.ui.people

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.Person
import tech.salroid.filmy.ui.common.components.ErrorWidget
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.common.components.PaginatedPreviewList
import tech.salroid.filmy.utility.toUserMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleScreen(
    modifier: Modifier = Modifier,
    viewModel: PeopleViewModel = hiltViewModel(),
    onPersonClick: (Int) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val people = viewModel.people.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Popular People") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            PeopleContent(people = people, onPersonClick = onPersonClick)
        }
    }
}

@Composable
private fun PeopleContent(
    people: LazyPagingItems<Person>,
    onPersonClick: (Int) -> Unit
) {
    when (val state = people.loadState.refresh) {
        is LoadState.Loading -> {
            LoadingWidget(modifier = Modifier.fillMaxSize())
        }

        is LoadState.Error -> {
            ErrorWidget(
                modifier = Modifier.fillMaxSize(),
                message = state.error.toUserMessage(),
                onRetryClick = { people.retry() }
            )
        }

        else -> {
            if (people.itemCount == 0 &&
                people.loadState.append is LoadState.NotLoading &&
                people.loadState.append.endOfPaginationReached
            ) {
                ErrorWidget(
                    modifier = Modifier.fillMaxSize(),
                    message = "No people found",
                    onRetryClick = { people.refresh() }
                )
            } else {
                PaginatedPreviewList(
                    modifier = Modifier.fillMaxSize(),
                    items = people,
                    content = { person ->
                        PersonItem(person = person, onClick = { onPersonClick(person.id) })
                    }
                )
            }
        }
    }
}

@Composable
private fun PersonItem(person: Person, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w185${person.profilePath}",
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.default_avatar),
            error = painterResource(R.drawable.default_avatar)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = person.name ?: "",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        if (!person.knownForDepartment.isNullOrBlank()) {
            Text(
                text = person.knownForDepartment ?: "",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
