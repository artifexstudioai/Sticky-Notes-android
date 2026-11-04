package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StickyColor
import com.example.data.StickyNote
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StickyNoteCard(
    note: StickyNote,
    onClick: () -> Unit,
    onPinToggle: () -> Unit,
    onPinToWidget: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onColorChange: (StickyColor) -> Unit,
    onChecklistToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val color = note.color
    var showMenu by remember { mutableStateOf(false) }
    var showColorSubmenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (note.isPinned) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .testTag("note_card_${note.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.composeBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Bar with decorative tape strip and pin status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Realistic translucent tape strip
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 10.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.55f))
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.isPinned) {
                        Icon(
                            Icons.Filled.PushPin,
                            contentDescription = "Pinned Note",
                            tint = Color(0xFFE53935),
                            modifier = Modifier
                                .size(18.dp)
                                .testTag("pinned_indicator_${note.id}")
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("note_menu_btn_${note.id}")
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = color.composeOnBg.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = {
                                showMenu = false
                                showColorSubmenu = false
                            }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (note.isPinned) "Unpin Note" else "Pin to Top") },
                                leadingIcon = {
                                    Icon(
                                        if (note.isPinned) Icons.Outlined.PushPin else Icons.Filled.PushPin,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onPinToggle()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Pin to Home Screen") },
                                leadingIcon = {
                                    Icon(Icons.Default.Widgets, contentDescription = null)
                                },
                                onClick = {
                                    showMenu = false
                                    onPinToWidget()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Change Color") },
                                leadingIcon = {
                                    Icon(Icons.Default.Palette, contentDescription = null)
                                },
                                onClick = {
                                    showColorSubmenu = !showColorSubmenu
                                }
                            )

                            if (showColorSubmenu) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    StickyColor.entries.forEach { optionColor ->
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(optionColor.composeBg)
                                                .clickable {
                                                    onColorChange(optionColor)
                                                    showMenu = false
                                                    showColorSubmenu = false
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (note.colorName == optionColor.name) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = optionColor.composeOnBg,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            DropdownMenuItem(
                                text = { Text("Duplicate") },
                                leadingIcon = {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                                },
                                onClick = {
                                    showMenu = false
                                    onDuplicate()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Delete", color = Color(0xFFD32F2F)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.DeleteOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFD32F2F)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Note Title
            if (note.title.isNotBlank()) {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = color.composeOnBg,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
            }

            // Note Content or Checklist items
            if (note.isChecklist) {
                val items = note.checklistItems
                if (items.isEmpty()) {
                    Text(
                        text = "Empty checklist",
                        fontSize = 13.sp,
                        color = color.composeOnBg.copy(alpha = 0.6f)
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items.take(4).forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onChecklistToggle(item.id) }
                            ) {
                                Checkbox(
                                    checked = item.isChecked,
                                    onCheckedChange = { onChecklistToggle(item.id) },
                                    modifier = Modifier.size(24.dp),
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = color.composeAccent,
                                        uncheckedColor = color.composeOnBg.copy(alpha = 0.6f),
                                        checkmarkColor = color.composeOnBg
                                    )
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = item.text,
                                    fontSize = 13.sp,
                                    color = if (item.isChecked) color.composeOnBg.copy(alpha = 0.5f) else color.composeOnBg,
                                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        if (items.size > 4) {
                            Text(
                                text = "+ ${items.size - 4} more items",
                                fontSize = 11.sp,
                                color = color.composeOnBg.copy(alpha = 0.7f),
                                modifier = Modifier.padding(start = 30.dp, top = 2.dp)
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = note.content.ifBlank { "Tap to write note..." },
                    fontSize = 13.sp,
                    color = color.composeOnBg.copy(alpha = if (note.content.isBlank()) 0.5f else 0.85f),
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            // Bottom bar: Timestamp & Home Screen Widget button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val sdf = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }
                Text(
                    text = sdf.format(Date(note.updatedAt)),
                    fontSize = 11.sp,
                    color = color.composeOnBg.copy(alpha = 0.6f)
                )

                // Quick Widget Pin Button
                IconButton(
                    onClick = onPinToWidget,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("pin_widget_btn_${note.id}")
                ) {
                    Icon(
                        Icons.Default.Widgets,
                        contentDescription = "Pin to Home Screen Widget",
                        tint = color.composeAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
