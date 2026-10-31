package com.example.motivationapp.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motivationapp.data.Task
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Runs one spin of the wheel and suspends until it has stopped.
 *
 * The row at index i of the wheel shows tasks[i % taskCount]. The wheel only
 * ever scrolls downward and ends exactly on a row showing tasks[winnerPos].
 *
 * @param currentCenter index of the row currently in the center of the viewport
 * @param winnerPos position (in the task list) of the task that should win
 */
private suspend fun spinWheel(
    listState: LazyListState,
    taskCount: Int,
    currentCenter: Int,
    winnerPos: Int,
    minSpinSteps: Int,
    itemHeightPx: Float,
    durationMillis: Int = 4500
) {
    // Jump (invisibly) to the same task one cycle in, so there is always
    // enough track ahead of us.
    val startIndex = (currentCenter % taskCount) + taskCount
    listState.scrollToItem(startIndex - 1)

    // First row showing the winner that is at least minSpinSteps below the start.
    val earliest = startIndex + minSpinSteps
    val targetIndex =
        earliest + ((winnerPos - earliest) % taskCount + taskCount) % taskCount
    val steps = targetIndex - startIndex

    // Animate the scroll offset downward only: fast at first, slowing at the end.
    val totalPx = steps * itemHeightPx
    val progress = Animatable(0f)
    var lastValue = 0f
    progress.animateTo(
        targetValue = totalPx,
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = CubicBezierEasing(0.35f, 0.05f, 0.1f, 1f)
        )
    ) {
        val delta = (value - lastValue).coerceAtLeast(0f)
        lastValue = value
        listState.dispatchRawDelta(delta)
    }

    // Snap exactly onto the winning row (removes rounding drift).
    listState.scrollToItem(targetIndex - 1, 0)
}

@Composable
fun TaskSlotMachine(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    var isSpinning by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<String?>(null) }
    var winningTaskId by remember { mutableStateOf<Int?>(null) }

    // Makes sure the automatic spin on startup only happens once.
    var hasSpunOnStartup by rememberSaveable { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val itemHeight = 64.dp
    val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }

    val taskCount = tasks.size

    // Minimum number of rows the wheel travels per spin.
    val minSpinSteps = maxOf(40, taskCount * 2)

    // The wheel repeats the task list. It must be long enough that, after
    // jumping back to the first cycles, the longest spin still fits.
    val wheelSize = maxOf(200, taskCount * 4 + minSpinSteps + 10)

    val wheelItems = remember(tasks) {
        if (tasks.isEmpty()) emptyList()
        else List(wheelSize) { index -> tasks[index % tasks.size] }
    }

    // Index of the row closest to the center of the viewport.
    val centerIndex by remember {
        derivedStateOf {
            val layout = listState.layoutInfo
            val viewportCenter =
                (layout.viewportStartOffset + layout.viewportEndOffset) / 2

            layout.visibleItemsInfo.minByOrNull { item ->
                kotlin.math.abs((item.offset + item.size / 2) - viewportCenter)
            }?.index ?: 0
        }
    }

    // Picks a random task, spins the wheel and shows the result.
    fun spin() {
        if (tasks.isEmpty() || isSpinning) return

        scope.launch {
            isSpinning = true
            selectedTask = null
            winningTaskId = null
            try {
                val winnerPos = Random.nextInt(taskCount)
                val winner = tasks[winnerPos]

                spinWheel(
                    listState = listState,
                    taskCount = taskCount,
                    currentCenter = centerIndex,
                    winnerPos = winnerPos,
                    minSpinSteps = minSpinSteps,
                    itemHeightPx = itemHeightPx
                )

                selectedTask = winner.name
                winningTaskId = winner.id
            } finally {
                isSpinning = false
            }
        }
    }

    // Start with a task centered, and spin once when the app starts
    // (as soon as tasks are available).
    LaunchedEffect(wheelItems) {
        if (wheelItems.isNotEmpty()) {
            // Start at the second cycle so rows exist above the center row.
            listState.scrollToItem((taskCount - 1).coerceAtLeast(0))

            if (!hasSpunOnStartup) {
                hasSpunOnStartup = true
                spin()
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "WHAT SHALL WE DO?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9275B8)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFEFF6),
                            Color(0xFFEDE4FF)
                        )
                    )
                )
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight * 3),
                horizontalAlignment = Alignment.CenterHorizontally,
                userScrollEnabled = false
            ) {
                itemsIndexed(
                    items = wheelItems,
                    key = { index, _ -> index }
                ) { index, task ->
                    val distance = kotlin.math.abs(index - centerIndex)
                    val isCenter = distance == 0

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .scale(if (isCenter) 1f else 0.88f)
                            .alpha(
                                when (distance) {
                                    0 -> 1f
                                    1 -> 0.42f
                                    else -> 0.18f
                                }
                            )
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (isCenter) Color.White else Color(0xFFE1D2FA)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = task.name,
                            color = if (isCenter) Color(0xFF6035B0) else Color(0xFF8875A5),
                            fontSize = if (isCenter) 21.sp else 17.sp,
                            fontWeight = if (isCenter) FontWeight.ExtraBold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }

            // Fade the top and bottom edges to emphasize the center row.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight * 3)
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color(0xD9F5EDFF),
                                0.22f to Color.Transparent,
                                0.78f to Color.Transparent,
                                1f to Color(0xD9F5EDFF)
                            )
                        )
                    )
            )
        }

        Button(
            onClick = { spin() },
            enabled = tasks.isNotEmpty() && !isSpinning,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B5CF6),
                disabledContainerColor = Color(0xFFB9A2E8)
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isSpinning) "SPINNING..." else "Spin again",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        when {
            tasks.isEmpty() -> {
                Text(
                    text = "Add some tasks first!",
                    color = Color(0xFF806A96),
                    fontSize = 16.sp
                )
            }

            selectedTask != null -> {
                Text(
                    text = "Your next adventure: $selectedTask",
                    color = Color(0xFF6035B0),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}