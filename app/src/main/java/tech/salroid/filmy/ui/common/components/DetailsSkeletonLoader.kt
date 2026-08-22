package tech.salroid.filmy.ui.common.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.app.Activity
import tech.salroid.filmy.ui.theme.AppTheme
import tech.salroid.filmy.utility.themeSystemBars

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsSkeletonLoader(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.04f,
        targetValue = 0.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val shimmerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)

    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        activity?.themeSystemBars(
            lightStatusBar = false,
            isFullScreen = true,
            transparentStatus = true
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.themeSystemBars(
                lightStatusBar = true,
                isFullScreen = false,
                transparentStatus = false
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Backdrop
                /*Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(shimmerColor)
                )*/

                // Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 220.dp)
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(start = 156.dp, top = 16.dp, end = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(28.dp)
                                    .background(shimmerColor, RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(16.dp)
                                    .background(shimmerColor, RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.4f)
                                    .height(16.dp)
                                    .background(shimmerColor, RoundedCornerShape(4.dp))
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth(0.6f)
                                .height(20.dp)
                                .background(shimmerColor, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        repeat(4) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .padding(top = 8.dp)
                                    .fillMaxWidth(if (index == 3) 0.6f else 0.95f)
                                    .height(14.dp)
                                    .background(shimmerColor, RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }

                // Poster
                Card(
                    modifier = Modifier
                        .padding(start = 24.dp, top = 135.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp, 180.dp)
                            .background(shimmerColor)
                    )
                }
            }

            // Below header items (Ratings, Cast, etc.)
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(20.dp)
                        .background(shimmerColor, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(shimmerColor, RoundedCornerShape(8.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(20.dp)
                        .background(shimmerColor, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(120f / 180f)
                                .background(shimmerColor, RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsSkeletonLoaderPreview() {
    AppTheme {
        DetailsSkeletonLoader()
    }
}
