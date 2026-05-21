package com.example.presentation.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.components.HologramBadge
import com.example.presentation.navigation.Screen
import com.example.ui.theme.DeepSpaceBlack
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryCyberCyan
import com.example.ui.theme.VividMagenta
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.2f) }
    val rotation = remember { Animatable(0f) }
    val opacity = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Run staggered animations in parallel
        opacity.animateTo(1f, tween(1000))
        scale.animateTo(1f, tween(1200))
        rotation.animateTo(360f, tween(1600))
        
        delay(800) // Additional hover feel
        onNavigateToHome()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpaceBlack)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value)
        ) {
            // Neon intersecting rings creating a geometric AI Blend aperture logo
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .rotate(rotation.value),
                contentAlignment = Alignment.Center
            ) {
                // Ring 1
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 3.dp,
                            brush = Brush.sweepGradient(listOf(PrimaryElectricViolet, Color.Transparent, PrimaryElectricViolet)),
                            shape = CircleShape
                        )
                )
                // Ring 2 (Offset rotative)
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.85f)
                        .rotate(45f)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(listOf(SecondaryCyberCyan, Color.Transparent, SecondaryCyberCyan)),
                            shape = CircleShape
                        )
                )
                // Inner glowing node
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = Brush.radialGradient(listOf(VividMagenta, Color.Transparent)),
                            shape = CircleShape
                        )
                        .border(1.dp, VividMagenta, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Glowing titles
            Text(
                text = "AI BLEND",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    brush = Brush.linearGradient(
                        colors = listOf(PrimaryElectricViolet, SecondaryCyberCyan)
                    ),
                    letterSpacing = 4.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "NEURAL IMAGE ENGINE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryCyberCyan.copy(alpha = 0.8f),
                    letterSpacing = 2.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            HologramBadge(text = "v1.0 BETA")
        }
    }
}
