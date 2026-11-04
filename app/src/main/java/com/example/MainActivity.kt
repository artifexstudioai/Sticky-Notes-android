package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.StickyColor
import com.example.ui.NoteEditScreen
import com.example.ui.NoteListScreen
import com.example.ui.WidgetGuideDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.Screen
import com.example.viewmodel.StickyNotesViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: StickyNotesViewModel by viewModels()

    companion object {
        const val EXTRA_NOTE_ID = "EXTRA_NOTE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StickyNotesApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val noteId = intent?.getLongExtra(EXTRA_NOTE_ID, -1L) ?: -1L
        if (noteId != -1L) {
            viewModel.openNote(noteId)
        }
    }
}

@Composable
fun StickyNotesApp(viewModel: StickyNotesViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val filteredNotes by viewModel.filteredNotes.collectAsStateWithLifecycle()
    val allNotes by viewModel.allNotes.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedColorFilter by viewModel.selectedColorFilter.collectAsStateWithLifecycle()
    val showWidgetGuide by viewModel.showWidgetGuide.collectAsStateWithLifecycle()

    when (val screen = currentScreen) {
        is Screen.ListScreen -> {
            NoteListScreen(
                notes = filteredNotes,
                searchQuery = searchQuery,
                onSearchChange = { viewModel.setSearchQuery(it) },
                selectedColorFilter = selectedColorFilter,
                onColorFilterChange = { viewModel.setColorFilter(it) },
                onNoteClick = { noteId -> viewModel.openNote(noteId) },
                onCreateNewNote = { color, isChecklist -> viewModel.createNewNote(color, isChecklist) },
                onPinToggle = { note -> viewModel.togglePin(note) },
                onPinToWidget = { note -> viewModel.pinNoteToWidget(note) },
                onDeleteNote = { note -> viewModel.deleteNote(note) },
                onDuplicateNote = { note -> viewModel.duplicateNote(note) },
                onColorChange = { note, color -> viewModel.saveNote(note.copy(colorName = color.name)) },
                onChecklistToggle = { note, itemId -> viewModel.toggleChecklistItem(note, itemId) },
                onOpenWidgetGuide = { viewModel.showWidgetGuideDialog(true) }
            )
        }

        is Screen.EditScreen -> {
            val note = remember(screen.noteId, allNotes) {
                allNotes.find { it.id == screen.noteId }
            }

            if (note != null) {
                NoteEditScreen(
                    note = note,
                    onSaveNote = { updated -> viewModel.saveNote(updated) },
                    onDeleteNote = { toDelete -> viewModel.deleteNote(toDelete) },
                    onPinToggle = { toPin -> viewModel.togglePin(toPin) },
                    onPinToWidget = { toWidget -> viewModel.pinNoteToWidget(toWidget) },
                    onBack = { viewModel.navigateBack() }
                )
            } else {
                // If note not yet found (e.g. just inserted or deleted), fallback to list
                LaunchedEffect(Unit) {
                    if (allNotes.isNotEmpty()) {
                        viewModel.navigateBack()
                    }
                }
            }
        }
    }

    if (showWidgetGuide) {
        WidgetGuideDialog(
            onDismiss = { viewModel.showWidgetGuideDialog(false) },
            onPinFirstNote = {
                val noteToPin = filteredNotes.firstOrNull() ?: allNotes.firstOrNull()
                if (noteToPin != null) {
                    viewModel.pinNoteToWidget(noteToPin)
                } else {
                    viewModel.createNewNote(StickyColor.YELLOW, false)
                }
            }
        )
    }
}
