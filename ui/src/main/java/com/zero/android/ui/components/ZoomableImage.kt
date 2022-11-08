package com.zero.android.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import java.lang.Math.abs
import kotlin.math.withSign

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZoomableImage(
	modifier: Modifier = Modifier,
	imageUrl: String,
	scrollEnabled: MutableState<Boolean>,
	maxScale: Float = 1f,
	minScale: Float = 3f,
	contentScale: ContentScale = ContentScale.Fit,
	isRotation: Boolean = false,
	isZoomable: Boolean = true
) {
	val scale = remember { mutableStateOf(1f) }
	val rotationState = remember { mutableStateOf(1f) }
	val offsetX = remember { mutableStateOf(1f) }
	val offsetY = remember { mutableStateOf(1f) }
	val lazyState = rememberLazyListState()

	val configuration = LocalConfiguration.current
	val screenWidthPx = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }

	val coroutineScope = rememberCoroutineScope()
	Box(
		modifier =
		Modifier.clip(RectangleShape)
			.combinedClickable(
				interactionSource = remember { MutableInteractionSource() },
				indication = null,
				onClick = {},
				onDoubleClick = {
					if (scale.value >= 2f) {
						scale.value = 1f
						offsetX.value = 1f
						offsetY.value = 1f
						scrollEnabled.value = true
					} else scale.value = 3f
				}
			)
			.pointerInput(Unit) {
				if (isZoomable) {
					forEachGesture {
						awaitPointerEventScope {
							awaitFirstDown()
							do {
								val event = awaitPointerEvent()
								val zoom = event.calculateZoom()
								scale.value *= zoom
								if (scale.value > 1) {
									if (zoom > 1) {
										scrollEnabled.value = false
									}
									coroutineScope.launch { lazyState.setScrolling(false) }
									val offset = event.calculatePan()
									offsetX.value += offset.x
									offsetY.value += offset.y
									rotationState.value += event.calculateRotation()
									coroutineScope.launch { lazyState.setScrolling(true) }

									val imageWidth = screenWidthPx * scale.value
									val borderReached = imageWidth - screenWidthPx - 2 * abs(offsetX.value)
									scrollEnabled.value = borderReached <= 0
									if (borderReached < 0) {
										offsetX.value =
											((imageWidth - screenWidthPx) / 2f).withSign(offsetX.value)
										if (offset.x != 0f) offsetY.value -= offset.y
									}
								} else {
									scale.value = 1f
									offsetX.value = 1f
									offsetY.value = 1f
									scrollEnabled.value = true
								}
							} while (event.changes.any { it.pressed })
						}
					}
				}
			}
	) {
		AsyncImage(
			model = imageUrl,
			contentDescription = null,
			contentScale = contentScale,
			modifier =
			modifier.fillMaxSize().align(Alignment.Center).graphicsLayer {
				if (isZoomable) {
					scaleX = maxOf(maxScale, minOf(minScale, scale.value))
					scaleY = maxOf(maxScale, minOf(minScale, scale.value))
					if (isRotation) {
						rotationZ = rotationState.value
					}
					translationX = offsetX.value
					translationY = offsetY.value
				}
			}
		)
	}
}

suspend fun LazyListState.setScrolling(value: Boolean) {
	scroll(scrollPriority = MutatePriority.PreventUserInput) {
		when (value) {
			true -> Unit
			else -> awaitCancellation()
		}
	}
}
