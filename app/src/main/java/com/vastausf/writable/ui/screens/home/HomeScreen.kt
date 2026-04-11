package com.vastausf.writable.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vastausf.writable.R
import com.vastausf.writable.data.db.entry.DocumentEntity
import com.vastausf.writable.ui.theme.WritableTheme.colors
import com.vastausf.writable.ui.widgets.DocumentCover
import com.vastausf.writable.ui.widgets.DrawableBottomSheet
import com.vastausf.writable.ui.widgets.RoundButton
import com.vastausf.writable.ui.widgets.ThemedButton
import com.vastausf.writable.ui.widgets.TitleText
import com.vastausf.writable.ui.widgets.WritableDropdownMenu
import com.vastausf.writable.ui.widgets.WritableDropdownMenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onDocumentClick: (Long) -> Unit,
) {
    val documents by viewModel.documents.collectAsStateWithLifecycle()
    var selectedDocument by remember { mutableStateOf<DocumentEntity?>(null) }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { TitleText(stringResource(R.string.all_documents)) },
                colors = TopAppBarColors(
                    containerColor = colors.background,
                    scrolledContainerColor = colors.background,
                    navigationIconContentColor = colors.textAndIcons,
                    titleContentColor = colors.textAndIcons,
                    actionIconContentColor = colors.textAndIcons,
                    subtitleContentColor = colors.textAndIconsSecondary,
                ),
                actions = {
                    var dropdownMenuExpanded by remember { mutableStateOf(false) }

                    RoundButton(
                        icon = Icons.Rounded.Search,
                        contentDescription = R.string.search,
                    ) {

                    }
                    RoundButton(
                        icon = Icons.Rounded.MoreVert,
                        contentDescription = R.string.more,
                    ) {
                        dropdownMenuExpanded = true
                    }

                    WritableDropdownMenu(
                        expanded = dropdownMenuExpanded,
                        onDismissRequest = { dropdownMenuExpanded = false }
                    ) {
                        WritableDropdownMenuItem(
                            text = stringResource(R.string.settings),
                            icon = Icons.Rounded.Settings,
                        ) {
                            dropdownMenuExpanded = false
                        }
                        WritableDropdownMenuItem(
                            text = stringResource(R.string.settings),
                            icon = Icons.Rounded.Info,
                        ) {
                            dropdownMenuExpanded = false
                        }
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding),
        ) {
            LazyRow {
                item {
                    val documentTitle = stringResource(R.string.blank_document)

                    QuickAction(
                        painter = painterResource(R.drawable.ic_document),
                        text = stringResource(R.string.blank_document),
                    ) {
                        viewModel.createDocument(documentTitle)
                    }
                }
                item {
                    QuickAction(
                        painter = painterResource(R.drawable.ic_download),
                        text = stringResource(R.string.import_file),
                    ) {

                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 170.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(documents, key = { it.id }) { document ->
                    DocumentCard(
                        document = document,
                        onClick = {
                            onDocumentClick(document.id)
                        },
                        onEditClick = {
                            selectedDocument = document
                        },
                        onDeleteClick = {
                            viewModel.deleteDocument(document)
                        }
                    )
                }
            }
        }

        EditDocumentBottomSheet(
            document = selectedDocument,
            onDismissRequest = {
                selectedDocument = null
            },
            onSubmit = { document ->
                viewModel.updateDocument(document)

                selectedDocument = null
            }
        )
    }
}

@Composable
private fun EditDocumentBottomSheet(
    document: DocumentEntity?,
    onDismissRequest: () -> Unit,
    onSubmit: (DocumentEntity) -> Unit,
) {
    if (document == null) return

    DrawableBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        var selectedCoverColor by remember { mutableStateOf(Color(document.coverColor)) }
        var selectedSpineColor by remember { mutableStateOf(Color(document.spineColor)) }
        var selectedBookmarkColor by remember { mutableStateOf(Color(document.bookmarkColor)) }

        val palette = colors.palette

        DocumentCover(
            modifier = Modifier
                .aspectRatio(3f / 4f)
                .height(256.dp)
                .clip(RoundedCornerShape(8.dp)),
            cover = selectedCoverColor,
            spine = selectedSpineColor,
            bookmark = selectedBookmarkColor,
        )

        TitleText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(R.string.cover),
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(palette) { paletteColor ->
                DocumentCover(
                    modifier = Modifier
                        .height(128.dp)
                        .aspectRatio(3f / 4f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                selectedCoverColor = paletteColor
                            },
                        )
                        .padding(16.dp),
                    cover = paletteColor,
                    spine = selectedSpineColor,
                    bookmark = selectedBookmarkColor,
                )
            }
        }

        TitleText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(R.string.spine),
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(palette) { paletteColor ->
                DocumentCover(
                    modifier = Modifier
                        .height(128.dp)
                        .aspectRatio(3f / 4f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                selectedSpineColor = paletteColor
                            },
                        )
                        .padding(16.dp),
                    cover = selectedCoverColor,
                    spine = paletteColor,
                    bookmark = selectedBookmarkColor,
                )
            }
        }

        TitleText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(R.string.bookmark),
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(palette) { paletteColor ->
                DocumentCover(
                    modifier = Modifier
                        .height(128.dp)
                        .aspectRatio(3f / 4f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                selectedBookmarkColor = paletteColor
                            },
                        )
                        .padding(16.dp),
                    cover = selectedCoverColor,
                    spine = selectedSpineColor,
                    bookmark = paletteColor,
                )
            }
        }

        ThemedButton(
            text = stringResource(R.string.save),
        ) {
            onSubmit(document.copy(
                coverColor = selectedCoverColor.toArgb(),
                spineColor = selectedSpineColor.toArgb(),
                bookmarkColor = selectedBookmarkColor.toArgb(),
            ))
        }
    }
}
