package tech.salroid.filmy.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.ImageItem
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.common.components.PreviewList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    mediaId: Int,
    isTv: Boolean,
    viewModel: GalleryViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val images by viewModel.images.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var fullScreenIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(mediaId, isTv) {
        viewModel.loadImages(mediaId, isTv)
    }

    val currentList = if (selectedTab == 0) images?.backdrops.orEmpty() else images?.posters.orEmpty()

    if (fullScreenIndex != null) {
        FullScreenImagePager(
            images = currentList,
            startIndex = fullScreenIndex ?: 0,
            onDismiss = { fullScreenIndex = null }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when {
                isLoading -> LoadingWidget(modifier = Modifier.fillMaxSize())
                currentList.isEmpty() && images != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.list_empty))
                    }
                }
                else -> {
                    PreviewList(
                        modifier = Modifier.fillMaxSize().padding(top = 12.dp),
                        items = currentList.size,
                        header = {
                            SingleChoiceSegmentedButtonRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                SegmentedButton(
                                    selected = selectedTab == 0,
                                    onClick = { selectedTab = 0 },
                                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                                ) { Text("Backdrops") }
                                SegmentedButton(
                                    selected = selectedTab == 1,
                                    onClick = { selectedTab = 1 },
                                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                                ) { Text("Posters") }
                            }
                        }
                    ) { index ->
                        val image = currentList[index]
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w500${image.filePath}",
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(if (selectedTab == 0) 16f / 9f else 2f / 3f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { fullScreenIndex = index },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FullScreenImagePager(
    images: List<ImageItem>,
    startIndex: Int,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = startIndex) { images.size }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            ZoomableImage(imageUrl = "https://image.tmdb.org/t/p/original${images[page].filePath}")
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ZoomableImage(imageUrl: String) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(1f, 5f)
        scale = newScale
        if (newScale > 1f) {
            offsetX += panChange.x
            offsetY += panChange.y
        } else {
            // Snap back to centered once zoomed out, so the pager's own
            // swipe isn't fighting a leftover pan offset next time in.
            offsetX = 0f
            offsetY = 0f
        }
    }

    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            )
            .transformable(
                state = transformableState,
                // Only claim the pan gesture once actually zoomed in -
                // otherwise a plain horizontal drag at 1x is a page swipe,
                // and this modifier must not steal it from HorizontalPager.
                canPan = { scale > 1f }
            ),
        contentScale = ContentScale.Fit
    )
}
