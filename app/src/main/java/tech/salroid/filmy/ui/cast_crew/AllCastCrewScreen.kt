package tech.salroid.filmy.ui.cast_crew

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCastCrewScreen(
    id: Int,
    isTv: Boolean,
    title: String,
    viewModel: CastCrewViewModel = hiltViewModel(),
    onMemberClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val castAndCrew by viewModel.uiStateCastAndCrew.collectAsState()

    LaunchedEffect(id, isTv) {
        if (isTv) {
            viewModel.getCastAndCrewTv(id.toString())
        } else {
            viewModel.getCastAndCrew(id.toString())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        castAndCrew?.let { data ->
            val allMembers = data.cast.map { Member(it.id ?: 0, it.name ?: "", it.character ?: "", it.profilePath) } +
                             data.crew.map { Member(it.id ?: 0, it.name ?: "", it.job ?: "", it.profilePath) }

            if (allMembers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("No cast or crew found.")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(100.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(allMembers) { member ->
                        MemberItem(member, onClick = { onMemberClick(member.id) })
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

data class Member(val id: Int, val name: String, val role: String, val profilePath: String?)

@Composable
fun MemberItem(member: Member, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "http://image.tmdb.org/t/p/w185${member.profilePath}",
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.default_avatar),
            error = painterResource(R.drawable.default_avatar)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = member.name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = member.role,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AllCastCrewScreenPreview() {
    AppTheme {
        // Mocking behavior
    }
}
