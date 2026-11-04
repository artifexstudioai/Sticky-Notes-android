package com.example.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChecklistItem
import com.example.data.StickyColor
import com.example.data.StickyNote
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    note: StickyNote,
    onSaveNote: (StickyNote) -> Unit,
    onDeleteNote: (StickyNote) -> Unit,
    onPinToggle: (StickyNote) -> Unit,
    onPinToWidget: (StickyNote) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var title by remember(note.id) { mutableStateOf(note.title) }
    var content by remember(note.id) { mutableStateOf(note.content) }
    var selectedColor by remember(note.id) { mutableStateOf(note.color) }
    var isPinned by remember(note.id) { mutableStateOf(note.isPinned) }
    var isChecklist by remember(note.id) { mutableStateOf(note.isChecklist) }
    var checklistItems by remember(note.id) { mutableStateOf(note.checklistItems) }
    var newChecklistText by remember { mutableStateOf("") }
    var fontSize by remember(note.id) { mutableStateOf(note.fontSize) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val animatedBg by animateColorAsState(selectedColor.composeBg, label = "colorAnimation")

    fun triggerSave() {
        val updated = note.copy(
            title = title,
            content = content,
            colorName = selectedColor.name,
            isPinned = isPinned,
            isChecklist = isChecklist,
            checklistJson = ChecklistItem.toJsonArrayString(checklistItems),
            fontSize = fontSize
        )
        onSaveNote(updated)
    }

    BackHandler {
        triggerSave()
        onBack()
    }

    val textBodySize = when (fontSize) {
        "SMALL" -> 14.sp
        "LARGE" -> 20.sp
        else -> 16.sp
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            triggerSave()
                            onBack()
                        },
                        modifier = Modifier.testTag("edit_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = selectedColor.composeOnBg
                        )
                    }
                },
                actions = {
                    // Pin to Home Screen Widget
                    IconButton(
                        onClick = {
                            triggerSave()
                            onPinToWidget(
                                note.copy(
                                    title = title,
                                    content = content,
                                    colorName = selectedColor.name,
                                    isPinned = isPinned,
                                    isChecklist = isChecklist,
                                    checklistJson = ChecklistItem.toJsonArrayString(checklistItems)
                                )
                            )
                        },
                        modifier = Modifier.testTag("edit_pin_widget_button")
                    ) {
                        Icon(
                            Icons.Default.Widgets,
                            contentDescription = "Pin to Home Screen Widget",
                            tint = selectedColor.composeAccent
                        )
                    }

                    // Toggle Star / Pin
                    IconButton(
                        onClick = {
                            isPinned = !isPinned
                            triggerSave()
                        },
                        modifier = Modifier.testTag("edit_pin_note_button")
                    ) {
                        Icon(
                            if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin Note to Top",
                            tint = if (isPinned) Color(0xFFE53935) else selectedColor.composeOnBg.copy(alpha = 0.7f)
                        )
                    }

                    // Share Note
                    IconButton(
                        onClick = {
                            val shareBody = buildString {
                                if (title.isNotBlank()) appendLine(title)
                                if (isChecklist) {
                                    checklistItems.forEach {
                                        appendLine((if (it.isChecked) "[x] " else "[ ] ") + it.text)
                                    }
                                } else {
                                    append(content)
                                }
                            }
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareBody)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Sticky Note"))
                        },
                        modifier = Modifier.testTag("edit_share_button")
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share Note",
                            tint = selectedColor.composeOnBg.copy(alpha = 0.7f)
                        )
                    }

                    // Delete Note
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.testTag("edit_delete_button")
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Note",
                            tint = Color(0xFFD32F2F)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = animatedBg
                )
            )
        },
        containerColor = animatedBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // Realistic top tape decor
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 12.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.6f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(12.dp))

            // Color Selector Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(StickyColor.entries) { color ->
                        val isSelected = selectedColor == color
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(color.composeBg)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) color.composeAccent else Color.Gray.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedColor = color
                                    triggerSave()
                                }
                                .testTag("color_picker_${color.name}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = color.composeOnBg,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Font Size Selector Button
                IconButton(
                    onClick = {
                        fontSize = when (fontSize) {
                            "SMALL" -> "MEDIUM"
                            "MEDIUM" -> "LARGE"
                            else -> "SMALL"
                        }
                        triggerSave()
                    },
                    modifier = Modifier.testTag("font_size_toggle_button")
                ) {
                    Icon(
                        Icons.Default.FormatSize,
                        contentDescription = "Toggle Font Size: $fontSize",
                        tint = selectedColor.composeOnBg.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Format Type: Memo vs Checklist toggle
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = !isChecklist,
                    onClick = {
                        isChecklist = false
                        triggerSave()
                    },
                    label = { Text("Memo Note") },
                    leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = selectedColor.composeAccent.copy(alpha = 0.35f),
                        selectedLabelColor = selectedColor.composeOnBg
                    )
                )

                FilterChip(
                    selected = isChecklist,
                    onClick = {
                        isChecklist = true
                        if (checklistItems.isEmpty() && content.isNotBlank()) {
                            // Convert lines to checklist items
                            checklistItems = content.lines().filter { it.isNotBlank() }.map {
                                ChecklistItem(text = it.trim(), isChecked = false)
                            }
                        }
                        triggerSave()
                    },
                    label = { Text("Checklist") },
                    leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = selectedColor.composeAccent.copy(alpha = 0.35f),
                        selectedLabelColor = selectedColor.composeOnBg
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            // Title Input
            TextField(
                value = title,
                onValueChange = {
                    title = it
                    triggerSave()
                },
                placeholder = {
                    Text(
                        "Title",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = selectedColor.composeOnBg.copy(alpha = 0.45f)
                    )
                },
                textStyle = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = selectedColor.composeOnBg
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_title_input")
            )

            // Last edited time indicator
            val sdf = remember { SimpleDateFormat("MMMM d, h:mm a", Locale.getDefault()) }
            Text(
                text = "Last updated: ${sdf.format(Date(note.updatedAt))}",
                fontSize = 11.sp,
                color = selectedColor.composeOnBg.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )

            Spacer(Modifier.height(8.dp))

            // Main Editor Body
            if (isChecklist) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(checklistItems, key = { _, item -> item.id }) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = item.isChecked,
                                onCheckedChange = { isChecked ->
                                    val updated = checklistItems.toMutableList()
                                    updated[index] = item.copy(isChecked = isChecked)
                                    checklistItems = updated
                                    triggerSave()
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = selectedColor.composeAccent,
                                    uncheckedColor = selectedColor.composeOnBg.copy(alpha = 0.6f),
                                    checkmarkColor = selectedColor.composeOnBg
                                ),
                                modifier = Modifier.testTag("checklist_checkbox_$index")
                            )

                            OutlinedTextField(
                                value = item.text,
                                onValueChange = { newText ->
                                    val updated = checklistItems.toMutableList()
                                    updated[index] = item.copy(text = newText)
                                    checklistItems = updated
                                    triggerSave()
                                },
                                textStyle = TextStyle(
                                    fontSize = textBodySize,
                                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                                    color = if (item.isChecked) selectedColor.composeOnBg.copy(alpha = 0.5f) else selectedColor.composeOnBg
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = selectedColor.composeAccent,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("checklist_item_input_$index")
                            )

                            IconButton(
                                onClick = {
                                    val updated = checklistItems.toMutableList()
                                    updated.removeAt(index)
                                    checklistItems = updated
                                    triggerSave()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove item",
                                    tint = selectedColor.composeOnBg.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Add checklist item row
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = newChecklistText,
                                onValueChange = { newChecklistText = it },
                                placeholder = {
                                    Text(
                                        "+ Add new item...",
                                        fontSize = textBodySize,
                                        color = selectedColor.composeOnBg.copy(alpha = 0.5f)
                                    )
                                },
                                textStyle = TextStyle(
                                    fontSize = textBodySize,
                                    color = selectedColor.composeOnBg
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    if (newChecklistText.isNotBlank()) {
                                        checklistItems = checklistItems + ChecklistItem(
                                            text = newChecklistText.trim(),
                                            isChecked = false
                                        )
                                        newChecklistText = ""
                                        triggerSave()
                                    }
                                }),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = selectedColor.composeAccent,
                                    unfocusedBorderColor = selectedColor.composeOnBg.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("new_checklist_input")
                            )

                            Spacer(Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    if (newChecklistText.isNotBlank()) {
                                        checklistItems = checklistItems + ChecklistItem(
                                            text = newChecklistText.trim(),
                                            isChecked = false
                                        )
                                        newChecklistText = ""
                                        triggerSave()
                                    }
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(selectedColor.composeAccent)
                                    .testTag("add_checklist_item_button")
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add Item",
                                    tint = selectedColor.composeOnBg
                                )
                            }
                        }
                    }
                }
            } else {
                TextField(
                    value = content,
                    onValueChange = {
                        content = it
                        triggerSave()
                    },
                    placeholder = {
                        Text(
                            "Write your note here...",
                            fontSize = textBodySize,
                            color = selectedColor.composeOnBg.copy(alpha = 0.45f)
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = textBodySize,
                        color = selectedColor.composeOnBg,
                        lineHeight = 24.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("edit_content_input")
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Sticky Note?") },
            text = { Text("Are you sure you want to delete this sticky note? If it is shown on your home screen, the widget will update.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteNote(note)
                    },
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Delete", color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false },
                    modifier = Modifier.testTag("cancel_delete_button")
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
