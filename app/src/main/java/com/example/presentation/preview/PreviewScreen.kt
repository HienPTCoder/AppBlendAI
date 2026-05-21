package com.example.presentation.preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.GeneratedArtwork
import com.example.ui.theme.CustomGlassSurface
import com.example.ui.theme.DeepSpaceBlack
import com.example.ui.theme.PolarWhite
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryCyberCyan
import com.example.ui.theme.SpaceSlateDark
import com.example.ui.theme.VividMagenta

@Composable
fun PreviewScreen(
    viewModel: PreviewViewModel,
    artworkId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val artwork by viewModel.currentArtwork.collectAsState()
    val saveStatus by viewModel.saveStatus.collectAsState()

    LaunchedEffect(artworkId) {
        viewModel.loadArtwork(artworkId)
    }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Transform gesture state for beautiful zooming & panning!
    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += offsetChange
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpaceBlack)
            .testTag("preview_screen")
    ) {
        artwork?.let { item ->
            // 1. Zoomable/Pannable Main AI Image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(state = transformState)
                    .testTag("image_container"),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = item.imageUri,
                    contentDescription = item.prompt,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.75f)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .testTag("full_preview_image"),
                    contentScale = ContentScale.Fit
                )
            }

            // 2. Top Header Overlay (Back Button + Heart toggle)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 54.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .background(CustomGlassSurface, CircleShape)
                        .testTag("preview_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate Back",
                        tint = PolarWhite
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Favorite Toggle Icon
                    IconButton(
                        onClick = { viewModel.toggleFavorite() },
                        modifier = Modifier
                            .background(CustomGlassSurface, CircleShape)
                            .testTag("favorite_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Toggle",
                            tint = if (item.isFavorite) VividMagenta else PolarWhite
                        )
                    }

                    // Delete Icon
                    IconButton(
                        onClick = {
                            viewModel.deleteArtwork {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier
                            .background(CustomGlassSurface, CircleShape)
                            .testTag("preview_delete_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Artwork",
                            tint = Color.Red.copy(alpha = 0.82f)
                        )
                    }
                }
            }

            // 3. Bottom Glass Panel with prompt specifications
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(listOf(PolarWhite.copy(alpha = 0.08f), Color.Transparent)),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = CustomGlassSurface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Prompt Details",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryCyberCyan
                                )
                            )
                            
                            Text(
                                text = "${item.style.displayName} • ${item.aspectRatio.displayName}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PolarWhite.copy(alpha = 0.6f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = item.prompt,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PolarWhite,
                                lineHeight = 18.sp
                            ),
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action rows (Download or Share)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Share Button
                            IconButtonWithLabel(
                                icon = Icons.Default.Share,
                                label = "Share Image",
                                onClick = { viewModel.shareImage() },
                                modifier = Modifier.weight(1f).testTag("share_button")
                            )

                            // Save/Download Button
                            IconButtonWithLabel(
                                icon = Icons.Default.Download,
                                label = if (item.isDownloaded) "Saved" else "Save Gallery",
                                onClick = { viewModel.saveToGallery() },
                                modifier = Modifier.weight(1f).testTag("download_button")
                            )
                        }
                    }
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading Preview assets...", color = PolarWhite)
        }

        // Action Status Snackbar Pop-up
        saveStatus?.let { status ->
            Snackbar(
                action = {
                    TextButton(onClick = { viewModel.clearSaveStatus() }) {
                        Text("Okay", color = SecondaryCyberCyan)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 140.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(text = status)
            }
        }
    }
}

@Composable
fun IconButtonWithLabel(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(SpaceSlateDark, RoundedCornerShape(12.dp))
            .border(1.dp, PolarWhite.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryElectricViolet,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PolarWhite,
                fontSize = 13.sp
            )
        )
    }
}
