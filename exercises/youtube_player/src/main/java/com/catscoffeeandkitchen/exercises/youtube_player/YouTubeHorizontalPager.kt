package com.catscoffeeandkitchen.exercises.youtube_player

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.catscoffeeandkitchen.exercises.youtube_player.models.YouTubeVideo
import com.catscoffeeandkitchen.ui.components.Shimmer
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import com.catscoffeeandkitchen.ui.theme.Spacing
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun YouTubeHorizontalPager(
    search: String,
    startWithVolumeOn: Boolean,
    viewModel: YouTubeSearchViewModel = hiltViewModel<YouTubeSearchViewModel>()
) {
    val uiState by viewModel.videos.collectAsState()

    LaunchedEffect(search) {
        viewModel.search(search)
    }

    YouTubePagerContent(state = uiState, startWithVolumeOn)
}

@Composable
private fun YouTubePagerContent(
    state: YouTubeSearchViewModel.VideoUIState,
    startWithVolumeOn: Boolean,
) {
    val density = LocalDensity.current

    if (state.isLoading) {
        Column {
            Shimmer(
                containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
                shimmerColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(start = Spacing.Default, bottom = 4.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .fillMaxWidth(.8f)
                    .aspectRatio(16f / 9f)
            )

            Shimmer(
                containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
                shimmerColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(start = Spacing.Default, bottom = 4.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .fillMaxWidth(.7f)
                    .height(
                        with(density) {
                            MaterialTheme.typography.bodyLarge.fontSize.toDp()
                        }
                    )
            )

            Shimmer(
                containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
                shimmerColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(start = Spacing.Default)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .fillMaxWidth(.4f)
                    .height(
                        with(density) {
                            MaterialTheme.typography.labelSmall.fontSize.toDp()
                        }
                    )
            )
        }
    } else {
        state.videos?.let { videos ->
            YouTubeThumbnailsToVideo(videos, startWithVolumeOn)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun YouTubeThumbnailsToVideo(
    videos: List<YouTubeVideo>,
    startWithVolumeOn: Boolean,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(
        pageCount = { videos.size }
    )
    val width = LocalConfiguration.current.screenWidthDp
    val playerWidth = (width * .8).roundToInt().coerceAtLeast(200)
    var playing by remember { mutableStateOf<Int?>(null) }

    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        pageSpacing = 16.dp,
        pageSize = PageSize.Fixed(playerWidth.dp),
        contentPadding = PaddingValues(8.dp)
    ) { index ->
        val video = videos[index]

        Column(
            modifier = Modifier.padding(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp,
                bottom = 4.dp
            )
        ) {
            Crossfade(
                targetState = playing,
                label = "Thumbnail to Video"
            ) { playingPage ->
                if (playingPage != index) {
                    Box {
                        AsyncImage(
                            video.imageUrl,
                            "Thumbnail of YouTube video ${video.title}",
                            modifier = Modifier
                                .clip(CardDefaults.elevatedShape)
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                            contentScale = ContentScale.FillWidth,
                            placeholder = BrushPainter(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.outline.copy(alpha = .5f),
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .25f)
                                    )
                                )
                            )
                        )

                        IconButton(
                            onClick = { playing = index },
                            colors = IconButtonDefaults.iconButtonColors().copy(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                                    .copy(alpha = .75f)
                            ),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "play video",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                } else {
                    YouTubePlayer(
                        video.id,
                        playerWidth,
                        isVolumeOn = startWithVolumeOn
                    )
                }
            }

            Column(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    video.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )

                Text(
                    video.channelName,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview
@Composable
private fun YouTubePagerLoadingPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 32.dp)
        ) {
            YouTubePagerContent(
                state = YouTubeSearchViewModel.VideoUIState(isLoading = true),
                startWithVolumeOn = false
            )
        }
    }
}

@Preview
@Composable
private fun YouTubePagerPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 32.dp)
        ) {
            YouTubePagerContent(
                YouTubeSearchViewModel.VideoUIState(
                    videos = listOf(
                        YouTubeVideo(
                            id = "1",
                            title = "A Good Video",
                            channelName = "Channel Name",
                            description = "Something about the video",
                            imageUrl = ""
                        ),
                        YouTubeVideo(
                            id = "2",
                            title = "Another Video",
                            channelName = "Channel Name",
                            description = "Something about the video",
                            imageUrl = ""
                        ),
                        YouTubeVideo(
                            id = "3",
                            title = "Third Video",
                            channelName = "Channel Name",
                            description = "Something about the video",
                            imageUrl = ""
                        )
                    )
                ),
                startWithVolumeOn = false
            )
        }
    }
}
