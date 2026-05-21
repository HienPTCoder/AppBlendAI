package com.example.presentation.settings

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.components.GlassCard
import com.example.ui.theme.CustomGlassSurface
import com.example.ui.theme.DeepSpaceBlack
import com.example.ui.theme.MutedCosmos
import com.example.ui.theme.PolarWhite
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryCyberCyan
import com.example.ui.theme.SpaceSlateDark
import com.example.ui.theme.VividMagenta
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val isDarkEnabled by viewModel.isDarkMode.collectAsState()
    val customKey by viewModel.customApiKey.collectAsState()
    val defaultQual by viewModel.defaultQuality.collectAsState()
    val appLang by viewModel.appLanguage.collectAsState()

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var keyInput by remember(customKey) { mutableStateOf(customKey ?: "") }
    var langMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpaceBlack)
            .testTag("settings_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(54.dp))

            // Subtitle Title
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = PolarWhite
                )
            )
            Text(
                text = "Configure engine parameters and preferences",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PolarWhite.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 1: Security Warning Alert (Mandated by setup and security constraints)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = VividMagenta.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(18.dp)
                    ),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x30E11D48) // Red slate glow
                )
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Security Alert",
                        tint = VividMagenta,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Decompile Security Caveat",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PolarWhite,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Security Warning: I have included your API keys in the generated APK file for this prototype. Please be aware that Android APKs can be easily decompiled, and these keys can be extracted by anyone who has access to the file. Do not share this APK file publicly or with unauthorized individuals to prevent potential misuse.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PolarWhite.copy(alpha = 0.72f),
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: General Cards Group
            SettingsGroupHeader(title = "App Appearance")
            Spacer(modifier = Modifier.height(8.dp))
            SettingsRow(
                icon = if (isDarkEnabled) Icons.Default.DarkMode else Icons.Default.LightMode,
                title = "Dark Theme Modalities",
                description = "Enable galactic space themes (always enabled in beta)",
                action = {
                    Switch(
                        checked = isDarkEnabled,
                        onCheckedChange = { viewModel.toggleDarkMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PolarWhite,
                            checkedTrackColor = PrimaryElectricViolet,
                            uncheckedThumbColor = MutedCosmos,
                            uncheckedTrackColor = SpaceSlateDark
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 3: API Key Override Inputs
            SettingsGroupHeader(title = "Gemini Access Profile")
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(listOf(PolarWhite.copy(alpha = 0.05f), Color.Transparent)),
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CustomGlassSurface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = SecondaryCyberCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Gemini API Key override",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PolarWhite,
                                fontSize = 14.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Don't have a key? The system will auto-use the AI Studio default key or fallback contextual synthesis. Enter a custom key below to utilize private quotas.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PolarWhite.copy(alpha = 0.5f),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = { keyInput = it },
                        placeholder = {
                            Text("AIzaSy...", color = PolarWhite.copy(alpha = 0.25f), fontSize = 12.sp)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("api_key_override_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryElectricViolet,
                            unfocusedBorderColor = PolarWhite.copy(alpha = 0.06f),
                            focusedContainerColor = SpaceSlateDark,
                            unfocusedContainerColor = SpaceSlateDark,
                            focusedTextColor = PolarWhite,
                            unfocusedTextColor = PolarWhite
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.saveCustomApiKey(keyInput.trim())
                            Toast.makeText(context, "API cluster settings updated!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SpaceSlateDark),
                        modifier = Modifier
                            .align(Alignment.End)
                            .border(1.dp, PolarWhite.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
                            .testTag("save_api_key_button")
                    ) {
                        Text("Save Profile Key", fontSize = 12.sp, color = SecondaryCyberCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 4: System controls
            SettingsGroupHeader(title = "Engine & Locales")
            Spacer(modifier = Modifier.height(8.dp))
            
            // Row: Default resolution choice
            SettingsRow(
                icon = Icons.Default.Speed,
                title = "Resolution Presets",
                description = "Standard vs ultra detailed render qualities",
                action = {
                    Row(
                        modifier = Modifier
                            .background(SpaceSlateDark, RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        listOf("STANDARD", "HD").forEach { q ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (defaultQual == q) PrimaryElectricViolet else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.updateDefaultQuality(q) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(text = q, color = PolarWhite, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Row: Language Switcher
            SettingsRow(
                icon = Icons.Default.Language,
                title = "Regional Language",
                description = "Translate visual tags and layouts",
                action = {
                    Box {
                        TextButton(onClick = { langMenuExpanded = true }) {
                            Text(
                                text = if (appLang == "en") "English 🌐" else "Tiếng Việt 🇻🇳",
                                color = SecondaryCyberCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        DropdownMenu(
                            expanded = langMenuExpanded,
                            onDismissRequest = { langMenuExpanded = false },
                            modifier = Modifier.background(SpaceSlateDark)
                        ) {
                            DropdownMenuItem(
                                text = { Text("English (USA)", color = PolarWhite) },
                                onClick = {
                                    viewModel.updateAppLanguage("en")
                                    langMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Tiếng Việt (VN)", color = PolarWhite) },
                                onClick = {
                                    viewModel.updateAppLanguage("vi")
                                    langMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Row: Clear Cache
            SettingsRow(
                icon = Icons.Default.Cached,
                title = "Purge Cache files",
                description = "Clear downloaded image caches instantly",
                action = {
                    TextButton(
                        onClick = {
                            viewModel.clearAppCache { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.testTag("clear_cache_settings_row")
                    ) {
                        Text("Purge", color = Color.Red.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                    }
                }
            )

            Spacer(modifier = Modifier.height(34.dp))

            // Section 5: Software specifications footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AI BLEND PRO • VERSION 1.0.0 (BETA)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PolarWhite.copy(alpha = 0.35f),
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Crafted inside Android Sandboxes under MVVM",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PolarWhite.copy(alpha = 0.25f),
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
fun SettingsGroupHeader(
    title: String
) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            color = PrimaryElectricViolet,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    )
}

@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    action: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(PolarWhite.copy(alpha = 0.04f), Color.Transparent)),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CustomGlassSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.04f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryElectricViolet,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PolarWhite,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PolarWhite.copy(alpha = 0.5f),
                            fontSize = 11.5.sp,
                            lineHeight = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(10.dp))
            action()
        }
    }
}

private val TextOverflowEllipsis = TextOverflow.Ellipsis
