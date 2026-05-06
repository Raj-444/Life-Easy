package com.example.lifeeasy.ui.screens.auth

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeeasy.R
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.theme.spacing
import com.example.lifeeasy.ui.theme.Primary
import com.example.lifeeasy.ui.theme.Accent
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Plan Your Day",
            description = "Stay ahead of your schedule with our intuitive daily planner. Manage tasks, events, and focus time seamlessly.",
            icon = Icons.Default.CalendarMonth,
            iconColor = Primary
        ),
        OnboardingPage(
            title = "Track Progress",
            description = "Monitor your habits, attendance, and finances. See your growth with beautiful charts and insights.",
            icon = Icons.Default.TrendingUp,
            iconColor = Accent
        ),
        OnboardingPage(
            title = "Stay Focused",
            description = "Use our Pomodoro timer and focus tools to maximize your productivity. Achieve more in less time.",
            icon = Icons.Default.Timer,
            iconColor = Color(0xFF4CAF50)
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val spacing = MaterialTheme.spacing

    AuthBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.large),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onFinished) {
                    Text("Skip", color = Color.White.copy(alpha = 0.6f))
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingContent(pages[page])
            }

            // Bottom Navigation
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.screenHorizontal, vertical = spacing.extraLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    repeat(pages.size) { i ->
                        val width = animateDpAsState(targetValue = if (pagerState.currentPage == i) 24.dp else 8.dp)
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width.value)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == i) Primary
                                    else Color.White.copy(alpha = 0.2f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.extraLarge))

                // Next Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onFinished()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}


@Composable
fun OnboardingContent(page: OnboardingPage) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.screenHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo at top
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "LifeEasy Logo",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(spacing.extraLarge))

        // Feature Icon
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(page.iconColor.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = page.iconColor
            )
        }

        Spacer(modifier = Modifier.height(spacing.extraLarge))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(spacing.medium))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}
