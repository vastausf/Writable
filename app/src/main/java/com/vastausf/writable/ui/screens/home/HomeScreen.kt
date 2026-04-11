package com.vastausf.writable.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vastausf.writable.R
import com.vastausf.writable.data.db.entry.DocumentEntity
import com.vastausf.writable.ui.theme.WritableTheme.colors
import com.vastausf.writable.ui.widgets.ContentText
import com.vastausf.writable.ui.widgets.DocumentCover
import com.vastausf.writable.ui.widgets.RoundButton
import com.vastausf.writable.ui.widgets.TitleText
import com.vastausf.writable.ui.widgets.WritableDropdownMenu
import com.vastausf.writable.ui.widgets.WritableDropdownMenuItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
    }
}

@Composable
private fun QuickAction(
    painter: Painter,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(8.dp, 4.dp)
            .background(
                color = colors.surface,
                shape = RoundedCornerShape(8.dp),
            )
            .border(
                border = BorderStroke(1.dp, colors.outline),
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onClick)
            .padding(16.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(32.dp),
            painter = painter,
            contentDescription = null,
        )
        Spacer(Modifier.size(8.dp))
        ContentText(text)
    }
}

@Composable
private fun DocumentCard(
    document: DocumentEntity,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick,
                    onLongClick = {
                        menuExpanded = true
                    },
                )
                .padding(8.dp)
        ) {
            DocumentCover(
                modifier = Modifier
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(8.dp)),
                cover = Color(document.coverColor),
                edge = Color(0xFF9A6949),
                bookmark = colors.accent,
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = document.title,
                color = colors.textAndIcons,
            )
            Text(
                text = document.openedAt.toFormattedDateTime(),
                color = colors.textAndIconsSecondary,
            )
        }

        WritableDropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            WritableDropdownMenuItem(
                text = stringResource(R.string.edit),
                icon = Icons.Rounded.Edit,
            ) {
                menuExpanded = false

                onEditClick()
            }

            WritableDropdownMenuItem(
                text = stringResource(R.string.delete),
                icon = Icons.Rounded.Delete,
            ) {
                menuExpanded = false

                onDeleteClick()
            }
        }
    }
}

private fun Long.toFormattedDateTime(): String {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.systemDefault(),
    )

    return DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm").format(dateTime)
}
