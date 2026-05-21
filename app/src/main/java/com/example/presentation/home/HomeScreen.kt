package com.example.presentation.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun HomeScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showProDialog by remember { mutableStateOf(false) }
    var clickedProFeatureName by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpaceBlack)
            .testTag("home_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Top Section (Headers + Logos + Settings Icon)
            item {
                Spacer(modifier = Modifier.height(54.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Monogram Logo
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    brush = Brush.radialGradient(listOf(PrimaryElectricViolet, VividMagenta)),
                                    shape = CircleShape
                                )
                                .border(1.dp, PolarWhite.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Æ",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = PolarWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AI Blend",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = PolarWhite,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clickable { 
                                            clickedProFeatureName = "AI Blend Premium Access"
                                            showProDialog = true 
                                        }
                                ) {
                                    HologramBadge(text = "PRO")
                                }
                            }
                            Text(
                                text = "Gen-2 Neural Studio",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = PolarWhite.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }

                    // Settings Button Icon
                    Box(
                        modifier = Modifier
                            .background(SpaceSlateDark, shape = CircleShape)
                            .border(1.dp, PolarWhite.copy(alpha = 0.08f), CircleShape)
                            .testTag("settings_icon_holder")
                    ) {
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier.testTag("home_settings_button")
                        ) {
                            // Use standard elegant representation
                            Text(
                                text = "⚙️",
                                fontSize = 20.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }

            // Large Hero Master Card (Launches AI Generation)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5f)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                listOf(PrimaryElectricViolet, VividMagenta, SecondaryCyberCyan)
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .clickable(onClick = onNavigateToCreate)
                        .testTag("hero_blend_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = SpaceSlateDark)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Ambient radial glowing background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            PrimaryElectricViolet.copy(alpha = 0.35f),
                                            Color.Transparent
                                        ),
                                        radius = 400f
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = SecondaryCyberCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                
                                Text(
                                    text = "GEMINI 2.5",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = SecondaryCyberCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Column {
                                Text(
                                    text = "AI Blend Studio",
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PolarWhite
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Create spectacular pictures from textual descriptions with smart style adaptation.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = PolarWhite.copy(alpha = 0.7f),
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Secondary Tools Header
            item {
                Text(
                    text = "Auxiliary Processors",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PolarWhite,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            // Grid of other features (Locked behind mock sheets / premium dialogs for cohesive fidelity)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Tool 1: Photo Mixer
                        Box(modifier = Modifier.weight(1f)) {
                            ToolGridCard(
                                icon = Icons.Default.Compare,
                                title = "Photo Mixer",
                                tagLine = "Merge Portraits",
                                onClick = {
                                    clickedProFeatureName = "Photo Mixer"
                                    showProDialog = true
                                }
                            )
                        }
                        
                        // Tool 2: Double Exposure
                        Box(modifier = Modifier.weight(1f)) {
                            ToolGridCard(
                                icon = Icons.Default.ColorLens,
                                title = "Double Exposure",
                                tagLine = "Contrast Duos",
                                onClick = {
                                    clickedProFeatureName = "Double Exposure"
                                    showProDialog = true
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Tool 3: Creative Editor
                        Box(modifier = Modifier.weight(1f)) {
                            ToolGridCard(
                                icon = Icons.Default.Brush,
                                title = "AI Editor",
                                tagLine = "Smart Inpaint",
                                onClick = {
                                    clickedProFeatureName = "AI Editor Pro"
                                    showProDialog = true
                                }
                            )
                        }
                        
                        // Tool 4: Shared Album
                        Box(modifier = Modifier.weight(1f)) {
                            ToolGridCard(
                                icon = Icons.Default.Collections,
                                title = "Local Albums",
                                tagLine = "Organize Assets",
                                onClick = {
                                    clickedProFeatureName = "Albums Sync"
                                    showProDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // Spacing bottom
            item {
                Spacer(modifier = Modifier.height(110.dp))
            }
        }

        // Show Pro upgrade prompt to the user
        if (showProDialog) {
            AlertDialog(
                onDismissRequest = { showProDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = VividMagenta,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = "Unlock $clickedProFeatureName",
                        color = PolarWhite,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "This premium generator requires active AI Blend Subscription. Our secure cluster will process requests using hyper-threaded graphic units.",
                        color = PolarWhite.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showProDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryElectricViolet)
                    ) {
                        Text("Upgrade - $4.99/mo", color = PolarWhite)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showProDialog = false }) {
                        Text("Later", color = MutedCosmosColor)
                    }
                },
                containerColor = SpaceSlateDark,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.border(1.dp, PrimaryElectricViolet.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            )
        }
    }
}

val MutedCosmosColor = Color(0xFF8F88A1)

@Composable
fun ToolGridCard(
    icon: ImageVector,
    title: String,
    tagLine: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.05f), Color.Transparent)),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CustomGlassSurface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(10.dp))
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = PrimaryElectricViolet
                    )
                }
                
                // Dot lock representation
                Text(
                    text = "🔒",
                    fontSize = 12.sp
                )
            }
            
            Spacer(modifier = Modifier.height(18.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PolarWhite,
                    fontSize = 15.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = tagLine,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PolarWhite.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            )
        }
    }
}
