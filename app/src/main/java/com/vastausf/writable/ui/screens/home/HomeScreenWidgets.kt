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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vastausf.writable.R
import com.vastausf.writable.data.db.entry.DocumentEntity
import com.vastausf.writable.ui.theme.WritableTheme.colors
import com.vastausf.writable.ui.utils.toFormattedDateTime
import com.vastausf.writable.ui.widgets.ContentText
import com.vastausf.writable.ui.widgets.DocumentCover
import com.vastausf.writable.ui.widgets.WritableDropdownMenu
import com.vastausf.writable.ui.widgets.WritableDropdownMenuItem

@Composable
fun QuickAction(
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
fun DocumentCard(
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
                .padding(8.dp),
        ) {
            DocumentCover(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(128.dp)
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(8.dp)),
                cover = Color(document.coverColor),
                spine = Color(document.spineColor),
                bookmark = Color(document.bookmarkColor),
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = document.title,
                color = colors.textAndIcons,
            )
            Text(
                text = document.openedAt.toFormattedDateTime("dd MMM yyyy hh:mm"),
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
