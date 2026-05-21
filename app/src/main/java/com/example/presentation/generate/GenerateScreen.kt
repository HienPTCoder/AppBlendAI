package com.example.presentation.generate

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.AspectRatio
import com.example.domain.model.ImageQuality
import com.example.domain.model.ImageReferenceMode
import com.example.domain.model.ImageStyle
import com.example.presentation.components.GlassCard
import com.example.presentation.components.GlowButton
import com.example.presentation.components.HologramBadge
import com.example.presentation.components.ShimmerPlaceholder
import com.example.ui.theme.CustomGlassSurface
import com.example.ui.theme.DeepSpaceBlack
import com.example.ui.theme.PolarWhite
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryCyberCyan
import com.example.ui.theme.SpaceSlateDark
import com.example.ui.theme.VividMagenta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(
    viewModel: GenerateViewModel,
    customApiKey: String?,
    onNavigateToResult: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val prompt by viewModel.prompt.collectAsState()
    val negativePrompt by viewModel.negativePrompt.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val selectedAspectRatio by viewModel.selectedAspectRatio.collectAsState()
    val selectedQuality by viewModel.selectedQuality.collectAsState()
    val generationState by viewModel.generationState.collectAsState()
    val referenceImageUri by viewModel.referenceImageUri.collectAsState()
    val referenceMode by viewModel.referenceMode.collectAsState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> viewModel.setReferenceImage(uri) }

    val scrollState = rememberScrollState()

    // Listening to generation success to open the result view automatically!
    LaunchedEffect(generationState) {
        if (generationState is GenerationState.Success) {
            val id = (generationState as GenerationState.Success).artwork.id
            onNavigateToResult(id)
            viewModel.resetState()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpaceBlack)
            .testTag("generate_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(54.dp))

            // Subtitle Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "AI Generator",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = PolarWhite
                        )
                    )
                    Text(
                        text = "Fusing concepts into vector reality",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PolarWhite.copy(alpha = 0.5f)
                        )
                    )
                }
                
                IconButton(
                    onClick = { viewModel.generateRandomPrompt() },
                    modifier = Modifier
                        .background(CustomGlassSurface, RoundedCornerShape(12.dp))
                        .testTag("surprise_button")
                ) {
                    Text(text = "🎲", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 1: Prompt Input
            Text(
                text = "Enter AI Text Prompt",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = PolarWhite
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = prompt,
                onValueChange = { viewModel.updatePrompt(it) },
                placeholder = {
                    Text(
                        text = "Describe anything you want to create (e.g. A gorgeous steampunk kitten flying on wings...)",
                        color = PolarWhite.copy(alpha = 0.35f),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("prompt_input"),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryElectricViolet,
                    unfocusedBorderColor = PolarWhite.copy(alpha = 0.08f),
                    focusedContainerColor = SpaceSlateDark.copy(alpha = 0.5f),
                    unfocusedContainerColor = SpaceSlateDark.copy(alpha = 0.5f),
                    focusedTextColor = PolarWhite,
                    unfocusedTextColor = PolarWhite
                ),
                trailingIcon = {
                    if (prompt.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updatePrompt("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Prompt", tint = PolarWhite.copy(alpha = 0.4f))
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Section 2: Reference Image (Optional)
            Text(
                text = "Reference Image (Optional)",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PolarWhite.copy(alpha = 0.8f)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (referenceImageUri == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .background(SpaceSlateDark.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .border(1.dp, PolarWhite.copy(alpha = 0.10f), RoundedCornerShape(16.dp))
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = PrimaryElectricViolet,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Upload from gallery",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PolarWhite.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SpaceSlateDark.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .border(1.dp, PrimaryElectricViolet.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = referenceImageUri,
                        contentDescription = "Reference image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reference image added",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PolarWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = "AI will use this as visual reference",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PolarWhite.copy(alpha = 0.45f),
                                fontSize = 11.sp
                            )
                        )
                    }
                    IconButton(onClick = { viewModel.setReferenceImage(null) }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Remove image",
                            tint = PolarWhite.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Mode toggle — only visible when a reference image is selected
            if (referenceImageUri != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CustomGlassSurface, RoundedCornerShape(14.dp))
                        .padding(4.dp)
                ) {
                    ImageReferenceMode.entries.forEach { mode ->
                        ReferenceModeTab(
                            mode = mode,
                            isSelected = referenceMode == mode,
                            onClick = { viewModel.setReferenceMode(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 3: Negative Prompt (Optional)
            Text(
                text = "Negative Prompt (What to Avoid)",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PolarWhite.copy(alpha = 0.8f)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = negativePrompt,
                onValueChange = { viewModel.updateNegativePrompt(it) },
                placeholder = {
                    Text(
                        text = "Ugly elements, blur, low quality, watermarks...",
                        color = PolarWhite.copy(alpha = 0.3f),
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("negative_prompt_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondaryCyberCyan,
                    unfocusedBorderColor = PolarWhite.copy(alpha = 0.05f),
                    focusedContainerColor = SpaceSlateDark.copy(alpha = 0.3f),
                    unfocusedContainerColor = SpaceSlateDark.copy(alpha = 0.3f),
                    focusedTextColor = PolarWhite,
                    unfocusedTextColor = PolarWhite
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 4: Select Styling
            Text(
                text = "Choose Graphic Style",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = PolarWhite
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("style_row")
            ) {
                items(ImageStyle.values()) { style ->
                    StyleChip(
                        style = style,
                        isSelected = selectedStyle == style,
                        onClick = { viewModel.selectStyle(style) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 4: Aspect Ratio
            Text(
                text = "Canvas Dimension Ratio",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = PolarWhite
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth().testTag("aspect_ratio_row"),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AspectRatio.values().forEach { ar ->
                    AspectRatioBox(
                        aspectRatio = ar,
                        isSelected = selectedAspectRatio == ar,
                        onClick = { viewModel.selectAspectRatio(ar) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 5: Image Quality Choice
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Image Quality",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = PolarWhite
                        )
                    )
                    Text(
                        text = "Render resolution fidelity",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PolarWhite.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    )
                }
                
                Row(
                    modifier = Modifier
                        .background(CustomGlassSurface, RoundedCornerShape(14.dp))
                        .padding(4.dp)
                ) {
                    ImageQuality.values().forEach { qual ->
                        QualityTab(
                            quality = qual,
                            isSelected = selectedQuality == qual,
                            onClick = { viewModel.selectQuality(qual) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(34.dp))

            // Section 6: Action Generate Button
            GlowButton(
                text = "Generate Artwork",
                onClick = { viewModel.generateImage(customApiKey) },
                modifier = Modifier.fillMaxWidth().testTag("generate_button"),
                enabled = prompt.isNotBlank() && generationState !is GenerationState.Loading
            )

            Spacer(modifier = Modifier.height(110.dp))
        }

        // Full Screen Generation Loading Overlay
        AnimatedVisibility(
            visible = generationState is GenerationState.Loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val currentMessage = (generationState as? GenerationState.Loading)?.message ?: "Processing networks..."
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    // Hologram spinning visual card
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryElectricViolet,
                            trackColor = SecondaryCyberCyan.copy(alpha = 0.15f),
                            strokeWidth = 4.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = VividMagenta,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))
                    Text(
                        text = "Generating Artwork",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = PolarWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PolarWhite.copy(alpha = 0.6f)
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "This will take about 5-15 seconds. Creating custom texture blends.",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = SecondaryCyberCyan,
                            fontSize = 11.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Error message popup/warning if any
        if (generationState is GenerationState.Error) {
            val errMsg = (generationState as GenerationState.Error).message
            androidx.compose.material3.Snackbar(
                action = {
                    TextButton(onClick = { viewModel.resetState() }) {
                        Text("Dismiss", color = SecondaryCyberCyan)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 90.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(text = errMsg)
            }
        }
    }
}

@Composable
fun StyleChip(
    style: ImageStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val outlineBrush = if (isSelected) {
        Brush.horizontalGradient(listOf(PrimaryElectricViolet, SecondaryCyberCyan))
    } else {
        Brush.linearGradient(listOf(PolarWhite.copy(alpha = 0.08f), PolarWhite.copy(alpha = 0.04f)))
    }
    
    Box(
        modifier = Modifier
            .background(
                if (isSelected) CustomGlassSurface else SpaceSlateDark,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                brush = outlineBrush,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("style_chip_${style.name}"),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = SecondaryCyberCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            } else {
                Text(text = "🎨  ", fontSize = 12.sp)
            }
            Text(
                text = style.displayName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) PolarWhite else PolarWhite.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
fun AspectRatioBox(
    aspectRatio: AspectRatio,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeBorder = if (isSelected) {
        Brush.horizontalGradient(listOf(PrimaryElectricViolet, VividMagenta))
    } else {
        Brush.linearGradient(listOf(PolarWhite.copy(alpha = 0.04f), Color.Transparent))
    }

    Column(
        modifier = modifier
            .background(SpaceSlateDark, RoundedCornerShape(16.dp))
            .border(1.dp, activeBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
            .testTag("ratio_box_${aspectRatio.name}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Miniature geometric shape outline representing the ratio visually!
        Box(
            modifier = Modifier
                .height(42.dp)
                .fillMaxWidth(0.6f)
                .background(CustomGlassSurface, RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = if (isSelected) SecondaryCyberCyan else PolarWhite.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Inner decorative rectangle representing the dynamic dimension ratio
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (aspectRatio.ratio > 1.0f) 0.8f else 0.4f)
                    .fillMaxHeight(if (aspectRatio.ratio < 1.0f) 0.8f else 0.4f)
                    .background(
                        if (isSelected) SecondaryCyberCyan.copy(alpha = 0.2f) else PolarWhite.copy(alpha = 0.05f)
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = aspectRatio.displayName,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) PolarWhite else PolarWhite.copy(alpha = 0.6f)
            )
        )
    }
}

@Composable
fun QualityTab(
    quality: ImageQuality,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) PrimaryElectricViolet else Color.Transparent,
                shape = RoundedCornerShape(11.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("quality_tab_${quality.name}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = quality.displayName,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) PolarWhite else PolarWhite.copy(alpha = 0.5f),
                fontSize = 13.sp
            )
        )
    }
}

@Composable
fun ReferenceModeTab(
    mode: ImageReferenceMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isSelected && mode == ImageReferenceMode.EDIT -> VividMagenta
        isSelected -> SecondaryCyberCyan
        else -> Color.Transparent
    }
    Column(
        modifier = modifier
            .background(bgColor, shape = RoundedCornerShape(11.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = mode.emoji, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = mode.displayName,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) DeepSpaceBlack else PolarWhite.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        )
        Text(
            text = mode.description,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isSelected) DeepSpaceBlack.copy(alpha = 0.7f) else PolarWhite.copy(alpha = 0.3f),
                fontSize = 10.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}
