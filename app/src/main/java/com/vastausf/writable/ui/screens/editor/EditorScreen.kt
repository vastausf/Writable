package com.vastausf.writable.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vastausf.writable.R
import com.vastausf.writable.data.db.entry.PageType
import com.vastausf.writable.ui.theme.WritableTheme.colors
import com.vastausf.writable.ui.widgets.ContentText
import com.vastausf.writable.ui.widgets.DrawingLayer
import com.vastausf.writable.ui.widgets.StylusPanel
import com.vastausf.writable.ui.widgets.ThemedIcon
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun EditorScreen(
    viewModel: EditorViewModel = hiltViewModel(),
) {
    val document by viewModel.document.collectAsStateWithLifecycle()
    var stylusStyle by remember { mutableStateOf(StylusStyle()) }

    LaunchedEffect(document) {
        document?.apply {
            stylusStyle = StylusStyle(
                color = stylusColor,
                width = stylusWidth,
            )
        }
    }

    Scaffold(
        containerColor = colors.background,
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            val pages by viewModel.pages.collectAsStateWithLifecycle()
            val listState = rememberLazyListState()

            val colors = colors.palette

            LaunchedEffect(listState) {
                snapshotFlow {
                    listState.layoutInfo.visibleItemsInfo
                        .map { it.index }
                }
                    .distinctUntilChanged()
                    .collect { viewModel.onVisiblePagesChanged(it) }
            }

            val totalPages = document?.pagesIds?.size ?: 0

            val density = LocalDensity.current
            var topSafeOffset by remember { mutableStateOf(0.dp) }

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    top = topSafeOffset,
                ),
            ) {
                items(totalPages) { page ->
                    RenderPage(
                        pageState = pages[page],
                        viewModel = viewModel,
                        stylusStyle = stylusStyle,
                    )
                }

                item {
                    EndPanel(
                        onNewPageClick = {
                            viewModel.addNewPage(
                                index = totalPages,
                            )
                        },
                    )
                }
            }

            StylusPanel(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .onSizeChanged { with(density) { topSafeOffset = it.height.toDp() } }
                    .padding(16.dp),
                selectedColor = Color(stylusStyle.color),
                onColorSelect = {
                    viewModel.updateStylusStyle(stylusStyle.copy(color = it.toArgb()))
                },
                selectedWidth = stylusStyle.width,
                onWidthSelect = {
                    viewModel.updateStylusStyle(stylusStyle.copy(width = it))
                },
                stylusColors = colors,
            )
        }
    }
}

@Composable
fun RenderPage(
    pageState: PageState?,
    viewModel: EditorViewModel,
    stylusStyle: StylusStyle,
) {
    if (pageState == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .background(colors.surface)
        )
    } else {
        Box(
            modifier = Modifier
                .padding(4.dp, 2.dp)
                .fillMaxWidth()
                .aspectRatio(pageState.page.ratio)
                .background(Color.White),
        ) {
            when (pageState.page.type) {
                PageType.Blank -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f),
                    )
                }
                PageType.Image -> {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize(),
                        model = pageState.page.url?.toUri(),
                        contentDescription = null,
                    )
                }
            }

            DrawingLayer(
                modifier = Modifier
                    .fillMaxSize(),
                strokes = pageState.strokes,
                stylusStyle = stylusStyle,
                onStrokeFinished = { stroke ->
                    viewModel.addStroke(
                        pageId = pageState.page.id,
                        stroke = stroke,
                    )
                },
            )
        }
    }
}

@Composable
fun EndPanel(
    modifier: Modifier = Modifier,
    onNewPageClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = colors.surface,
                    shape = RoundedCornerShape(4.dp),
                )
                .border(
                    width = 1.dp,
                    color = colors.outline,
                    shape = RoundedCornerShape(4.dp),
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onNewPageClick,
                )
                .padding(64.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ThemedIcon(
                modifier = Modifier
                    .size(32.dp),
                imageVector = Icons.Rounded.PostAdd,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.size(2.dp))
            ContentText(stringResource(R.string.new_page))
        }
    }
}
