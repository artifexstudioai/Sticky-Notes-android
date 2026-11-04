package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StickyColor
import com.example.data.StickyNote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    notes: List<StickyNote>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedColorFilter: StickyColor?,
    onColorFilterChange: (StickyColor?) -> Unit,
    onNoteClick: (Long) -> Unit,
    onCreateNewNote: (StickyColor, Boolean) -> Unit,
    onPinToggle: (StickyNote) -> Unit,
    onPinToWidget: (StickyNote) -> Unit,
    onDeleteNote: (StickyNote) -> Unit,
    onDuplicateNote: (StickyNote) -> Unit,
    onColorChange: (StickyNote, StickyColor) -> Unit,
    onChecklistToggle: (StickyNote, String) -> Unit,
    onOpenWidgetGuide: () -> Unit
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var showFabMenu by remember { mutableStateOf(false) }
    var showBanner by remember { mutableStateOf(true) }

    val pinnedNotes = remember(notes) { notes.filter { it.isPinned } }
    val otherNotes = remember(notes) { notes.filter { !it.isPinned } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchChange,
                            placeholder = { Text("Search notes & tasks...") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_text_field"),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchChange("") }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            }
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFFD54F)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📌", fontSize = 16.sp)
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Sticky Notes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isSearchActive = !isSearchActive
                            if (!isSearchActive) onSearchChange("")
                        },
                        modifier = Modifier.testTag("search_toggle_button")
                    ) {
                        Icon(
                            if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }

                    IconButton(
                        onClick = onOpenWidgetGuide,
                        modifier = Modifier.testTag("open_widget_guide_button")
                    ) {
                        Icon(
                            Icons.Default.Widgets,
                            contentDescription = "Home Screen Widgets Guide",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnimatedVisibility(visible = showFabMenu) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Option 1: Checklist Note
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                showFabMenu = false
                                onCreateNewNote(StickyColor.GREEN, true)
                            }
                        ) {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Text(
                                    "New Checklist",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            SmallFloatingActionButton(
                                onClick = {
                                    showFabMenu = false
                                    onCreateNewNote(StickyColor.GREEN, true)
                                },
                                containerColor = StickyColor.GREEN.composeBg,
                                modifier = Modifier.testTag("fab_create_checklist")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = "New Checklist")
                            }
                        }

                        // Option 2: Plain Note
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                showFabMenu = false
                                onCreateNewNote(StickyColor.YELLOW, false)
                            }
                        ) {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Text(
                                    "New Note",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            SmallFloatingActionButton(
                                onClick = {
                                    showFabMenu = false
                                    onCreateNewNote(StickyColor.YELLOW, false)
                                },
                                containerColor = StickyColor.YELLOW.composeBg,
                                modifier = Modifier.testTag("fab_create_memo")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = "New Memo")
                            }
                        }
                    }
                }

                ExtendedFloatingActionButton(
                    onClick = { showFabMenu = !showFabMenu },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Note") },
                    text = { Text("New Sticky") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("main_add_note_fab")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Color filter row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    FilterChip(
                        selected = selectedColorFilter == null,
                        onClick = { onColorFilterChange(null) },
                        label = { Text("All Notes") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("color_filter_all")
                    )
                }

                items(StickyColor.entries) { color ->
                    val isSelected = selectedColorFilter == color
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onColorFilterChange(if (isSelected) null else color)
                        },
                        label = { Text(color.displayName) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(color.composeBg)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color.composeBg,
                            selectedLabelColor = color.composeOnBg
                        ),
                        modifier = Modifier.testTag("color_filter_${color.name}")
                    )
                }
            }

            // Home Screen Widget Tip Banner
            AnimatedVisibility(visible = showBanner) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("widget_tip_banner"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Widgets,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Show notes as Home Screen widgets!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                "Tap the widget icon on any sticky note to place it right on your phone screen.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        IconButton(
                            onClick = { showBanner = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Empty state or Notes Grid
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(StickyColor.YELLOW.composeBg)
                                .shadow(6.dp, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📝", fontSize = 40.sp)
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedColorFilter != null) {
                                "No matching sticky notes found"
                            } else {
                                "Your sticky board is empty"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Tap the + button to create a note or checklist, then pin it to your home screen!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Pinned notes section
                    if (pinnedNotes.isNotEmpty() && selectedColorFilter == null && searchQuery.isBlank()) {
                        item(span = { GridItemSpan(2) }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    "PINNED NOTES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "(${pinnedNotes.size})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        items(pinnedNotes, key = { it.id }) { note ->
                            StickyNoteCard(
                                note = note,
                                onClick = { onNoteClick(note.id) },
                                onPinToggle = { onPinToggle(note) },
                                onPinToWidget = { onPinToWidget(note) },
                                onDelete = { onDeleteNote(note) },
                                onDuplicate = { onDuplicateNote(note) },
                                onColorChange = { onColorChange(note, it) },
                                onChecklistToggle = { onChecklistToggle(note, it) }
                            )
                        }

                        if (otherNotes.isNotEmpty()) {
                            item(span = { GridItemSpan(2) }) {
                                Text(
                                    "ALL NOTES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                                )
                            }
                        }
                    }

                    // Other notes section
                    val displayedOtherNotes = if (selectedColorFilter == null && searchQuery.isBlank()) {
                        otherNotes
                    } else {
                        notes
                    }

                    items(displayedOtherNotes, key = { it.id }) { note ->
                        StickyNoteCard(
                            note = note,
                            onClick = { onNoteClick(note.id) },
                            onPinToggle = { onPinToggle(note) },
                            onPinToWidget = { onPinToWidget(note) },
                            onDelete = { onDeleteNote(note) },
                            onDuplicate = { onDuplicateNote(note) },
                            onColorChange = { onColorChange(note, it) },
                            onChecklistToggle = { onChecklistToggle(note, it) }
                        )
                    }
                }
            }
        }
    }
}
