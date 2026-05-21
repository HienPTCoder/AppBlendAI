package com.example.presentation.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.GeneratedArtwork
import com.example.presentation.components.EmptyView
import com.example.ui.theme.CustomGlassSurface
import com.example.ui.theme.DeepSpaceBlack
import com.example.ui.theme.PolarWhite
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryCyberCyan
import com.example.ui.theme.SpaceSlateDark
import com.example.ui.theme.VividMagenta

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel,
    onNavigateToPreview: (Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemsFlow by viewModel.galleryItems.collectAsState()
    val rawFilter by viewModel.selectedFilter.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpaceBlack)
            .testTag("gallery_screen")
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header elements spanned across column grid
            item(span = { GridItemSpan(2) }) {
                Column {
                    Spacer(modifier = Modifier.height(54.dp))
                    Text(
                        text = "Exquisite Gallery",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = PolarWhite
                        )
                    )
                    Text(
                        text = "A compilation of your unique visual syntheses",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PolarWhite.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Filter Tab Layout Container
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CustomGlassSurface, RoundedCornerShape(16.dp))
                            .border(1.dp, PolarWhite.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(4.dp)
                            .testTag("gallery_filters_row"),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        GalleryFilter.values().forEach { filter ->
                            FilterTabItem(
                                name = when (filter) {
                                    GalleryFilter.ALL -> "Recent"
                                    GalleryFilter.FAVORITES -> "Favorites"
                                    GalleryFilter.DOWNLOADS -> "Downloads"
                                },
                                isSelected = rawFilter == filter,
                                onClick = { viewModel.selectFilter(filter) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Staggered list items or Empty ledger handles
            if (itemsFlow.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    EmptyView(
                        icon = Icons.Default.Collections,
                        title = "No Masterpieces",
                        description = "We couldn't locate any records matching this category filter. Synthesize custom designs inside our laboratory!",
                        actionButtonText = "Synthesize Now",
                        onActionClick = onNavigateToCreate,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                }
            } else {
                items(itemsFlow, key = { it.id }) { item ->
                    GalleryGridItem(
                        artwork = item,
                        onClick = { onNavigateToPreview(item.id) }
                    )
                }
            }

            // Decorative footer spacing
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(110.dp))
            }
        }
    }
}

@Composable
fun FilterTabItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) PrimaryElectricViolet else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
            .testTag("gallery_filter_$name"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) PolarWhite else PolarWhite.copy(alpha = 0.5f),
                fontSize = 13.sp
            )
        )
    }
}

@Composable
fun GalleryGridItem(
    artwork: GeneratedArtwork,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(when (artwork.aspectRatio) {
                com.example.domain.model.AspectRatio.RATIO_1_1 -> 1.0f
                com.example.domain.model.AspectRatio.RATIO_9_16 -> 0.65f
                com.example.domain.model.AspectRatio.RATIO_16_9 -> 1.5f
                com.example.domain.model.AspectRatio.RATIO_4_3 -> 1.3f
            })
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(PolarWhite.copy(alpha = 0.05f), Color.Transparent)),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .testTag("gallery_item_${artwork.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SpaceSlateDark)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Load local saved jpeg/png using Coil
            AsyncImage(
                model = artwork.imageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Neon glowing visual overlay for favorites item
            if (artwork.isFavorite) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .background(CustomGlassSurface, CircleShape)
                        .border(0.5.dp, VividMagenta.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = VividMagenta,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Bottom descriptive label representing ratio metadata
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f))
                        )
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = artwork.prompt,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PolarWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
