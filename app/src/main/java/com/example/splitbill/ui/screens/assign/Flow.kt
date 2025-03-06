package com.example.splitbill.ui.screens.assign

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Flow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: Dp = 0.dp,
    crossAxisSpacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val mainAxisSpacingPx = mainAxisSpacing.roundToPx()
        val crossAxisSpacingPx = crossAxisSpacing.roundToPx()

        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        val height = if (placeables.isEmpty()) 0 else {
            var currentX = 0
            var currentY = 0
            var rowHeight = 0

            placeables.forEach { placeable ->
                if (currentX + placeable.width > constraints.maxWidth) {
                    currentX = 0
                    currentY += rowHeight + crossAxisSpacingPx
                    rowHeight = 0
                }

                rowHeight = maxOf(rowHeight, placeable.height)
                currentX += placeable.width + mainAxisSpacingPx
            }

            currentY + rowHeight
        }

        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            var currentX = 0
            var currentY = 0
            var rowHeight = 0

            placeables.forEach { placeable ->
                if (currentX + placeable.width > constraints.maxWidth) {
                    currentX = 0
                    currentY += rowHeight + crossAxisSpacingPx
                    rowHeight = 0
                }

                placeable.placeRelative(currentX, currentY)
                rowHeight = maxOf(rowHeight, placeable.height)
                currentX += placeable.width + mainAxisSpacingPx
            }
        }
    }
}