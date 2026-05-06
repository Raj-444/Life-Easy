package com.example.lifeeasy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.lifeeasy.ui.theme.Primary
import com.example.lifeeasy.ui.theme.spacing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

/**
 * Premium Glassmorphism Card — blur is on background only, content stays crisp.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    blur: Dp = 20.dp,
    containerColor: Color = Color.White.copy(alpha = 0.08f),
    cornerRadius: Dp = 24.dp,
    shape: Shape? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val finalShape = shape ?: RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(finalShape)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                shape = finalShape
            )
    ) {
        // Background layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(containerColor)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                )
        )

        // Content layer — NO blur, always crisp and readable
        Column(
            modifier = Modifier
                .padding(MaterialTheme.spacing.medium)
        ) {
            content()
        }
    }
}

/**
 * Premium Animated Gradient Background for Auth & Feature Screens
 */
@Composable
fun AuthBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E17)) // Deep Dark Background
    ) {
        // Aesthetic Glow Orbs
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    // Primary Glow — top-left
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF6C63FF).copy(alpha = 0.20f),
                                Color.Transparent
                            ),
                            radius = 800f
                        ),
                        center = center.copy(x = size.width * 0.15f, y = size.height * 0.1f)
                    )
                    // Accent Glow — bottom-right
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF6584).copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            radius = 600f
                        ),
                        center = center.copy(x = size.width * 0.85f, y = size.height * 0.9f)
                    )
                }
        )
        content()
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: () -> Unit = {},
    // Compatibility fields
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {}
) {
    var localPasswordVisible by remember { mutableStateOf(passwordVisible) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, color = Color.White.copy(alpha = 0.6f)) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Primary) },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { 
                    localPasswordVisible = !localPasswordVisible
                    onTogglePassword()
                }) {
                    Icon(
                        imageVector = if (localPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !localPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(onAny = { onAction() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
            focusedLabelColor = Primary,
            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Primary
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )
}
