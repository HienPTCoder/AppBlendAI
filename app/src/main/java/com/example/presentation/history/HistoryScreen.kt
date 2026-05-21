package com.example.presentation.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.GeneratedArtwork
import com.example.presentation.components.EmptyView
import com.example.presentation.components.GlassCard
import com.example.presentation.components.HologramBadge
import com.example.ui.theme.CustomGlassSurface
import com.example.ui.theme.DeepSpaceBlack
import com.example.ui.theme.PolarWhite
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryCyberCyan
import com.example.ui.theme.SpaceSlateDark
import com.example.ui.theme.VividMagenta

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToPreview: (Long) -> Unit,
    onReusePrompt: (GeneratedArtwork) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchRule by viewModel.searchQuery.collectAsState()
    val sortByFavs by viewModel.sortByFavorites.collectAsState()
    val itemsFlow by viewModel.historyItems.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpaceBlack)
            .testTag("history_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(54.dp))

            // Subtitle Title
            Text(
                text = "Prompt Logbook",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = PolarWhite
                )
            )
            Text(
                text = "Track your historical artificial creations",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PolarWhite.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Search Bar & Filter layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchRule,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text("Search prompt or style...", color = PolarWhite.copy(alpha = 0.35f), fontSize = 13.sp)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("search_bar_input"),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = PolarWhite.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchRule.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = PolarWhite.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryElectricViolet,
                        unfocusedBorderColor = PolarWhite.copy(alpha = 0.06f),
                        focusedContainerColor = SpaceSlateDark,
                        unfocusedContainerColor = SpaceSlateDark,
                        focusedTextColor = PolarWhite,
                        unfocusedTextColor = PolarWhite
                    )
                )

                // Favorite filter selector button
                IconButton(
                    onClick = { viewModel.toggleSortByFavorites() },
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            if (sortByFavs) PrimaryElectricViolet else SpaceSlateDark,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (sortByFavs) SecondaryCyberCyan else PolarWhite.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .testTag("sort_favorites_button")
                ) {
                    Icon(
                        imageVector = if (sortByFavs) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Sort by Favorites first",
                        tint = if (sortByFavs) PolarWhite else PolarWhite.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main reactive list
            if (itemsFlow.isEmpty()) {
                val isSearching = searchRule.isNotEmpty()
                EmptyView(
                    icon = Icons.Default.HistoryEdu,
                    title = if (isSearching) "No Matches" else "No Prompt History",
                    description = if (isSearching) "We couldn't search any matching entries. Please verify the query and try again." else "Your generated artwork ledger is empty. Tap the Create button below to synthesize your first item!",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("history_list"),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(itemsFlow, key = { it.id }) { item ->
                        HistoryItemCard(
                            artwork = item,
                            onClick = { onNavigateToPreview(item.id) },
                            onReuse = { onReusePrompt(item) },
                            onDelete = { viewModel.deleteHistoryItem(item.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite(item.id, !item.isFavorite) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(110.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    artwork: GeneratedArtwork,
    onClick: () -> Unit,
    onReuse: () -> Unit,
    onDelete: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(PolarWhite.copy(alpha = 0.04f), Color.Transparent)),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .testTag("history_card_${artwork.id}")
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = CustomGlassSurface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail image coil loader
            AsyncImage(
                model = artwork.imageUri,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, PolarWhite.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .testTag("history_thumbnail_${artwork.id}"),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HologramBadge(text = artwork.style.displayName)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = artwork.aspectRatio.displayName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 10.sp,
                                color = PolarWhite.copy(alpha = 0.4f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = artwork.prompt,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PolarWhite,
                        lineHeight = 16.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onReuse,
                        modifier = Modifier.height(34.dp).testTag("reuse_button_${artwork.id}")
                    ) {
                        Text("⚡ Reuse Prompt", fontSize = 12.sp, color = SecondaryCyberCyan, fontWeight = FontWeight.Bold)
                    }

                    Row {
                        IconButton(onClick = onFavoriteToggle, modifier = Modifier.size(34.dp)) {
                            Icon(
                                imageVector = if (artwork.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite Toggle",
                                tint = if (artwork.isFavorite) VividMagenta else PolarWhite.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(onClick = onDelete, modifier = Modifier.size(34.dp).testTag("delete_item_${artwork.id}")) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete item",
                                tint = PolarWhite.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
