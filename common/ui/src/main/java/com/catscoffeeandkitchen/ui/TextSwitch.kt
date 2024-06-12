package com.catscoffeeandkitchen.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.runtime.getValue

@Composable
fun TextSwitch(
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedIndex,
        indicator = { tabPositions ->
            val currentTabWidth by animateDpAsState(
                targetValue = tabPositions[selectedIndex].width,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
            )
            val indicatorOffset by animateDpAsState(
                targetValue = tabPositions[selectedIndex].left,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .fillMaxHeight()
                    .background(color = MaterialTheme.colorScheme.primary.copy(alpha = .3f))
            ) {
                Box(
                    Modifier
                        .padding(4.dp)
                        .width(currentTabWidth - 8.dp)
                        .offset(x = indicatorOffset)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color = MaterialTheme.colorScheme.primary)
                        .fillMaxHeight()
                )
            }
        },
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = .3f)),
        tabs = {
            options.forEachIndexed { index, title ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { onSelectIndex(index) },
                    text = { Text(
                        text = title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = if (selectedIndex == index) MaterialTheme.colorScheme.onPrimary else
                            MaterialTheme.colorScheme.primary
                    ) },
                    modifier = Modifier.zIndex(3f)
                )
            }
        }
    )

}

