package com.example.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChecklistItem
import com.example.data.StickyColor
import com.example.data.StickyNote
import com.example.data.StickyNoteDatabase
import com.example.data.StickyNoteRepository
import com.example.widget.StickyNoteWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object ListScreen : Screen()
    data class EditScreen(val noteId: Long) : Screen()
}

class StickyNotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StickyNoteRepository

    init {
        val dao = StickyNoteDatabase.getDatabase(application).stickyNoteDao()
        repository = StickyNoteRepository(dao)
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedColorFilter = MutableStateFlow<StickyColor?>(null)
    val selectedColorFilter: StateFlow<StickyColor?> = _selectedColorFilter

    val allNotes: StateFlow<List<StickyNote>> = repository.allNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredNotes: StateFlow<List<StickyNote>> = combine(
        allNotes,
        _searchQuery,
        _selectedColorFilter
    ) { notes, query, colorFilter ->
        notes.filter { note ->
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                note.title.contains(query, ignoreCase = true) ||
                        note.content.contains(query, ignoreCase = true) ||
                        note.checklistItems.any { it.text.contains(query, ignoreCase = true) }
            }
            val matchesColor = colorFilter == null || note.colorName.equals(colorFilter.name, ignoreCase = true)
            matchesQuery && matchesColor
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _currentScreen = MutableStateFlow<Screen>(Screen.ListScreen)
    val currentScreen: StateFlow<Screen> = _currentScreen

    private val _showWidgetGuide = MutableStateFlow(false)
    val showWidgetGuide: StateFlow<Boolean> = _showWidgetGuide

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setColorFilter(color: StickyColor?) {
        _selectedColorFilter.value = color
    }

    fun openNote(noteId: Long) {
        _currentScreen.value = Screen.EditScreen(noteId)
    }

    fun navigateBack() {
        _currentScreen.value = Screen.ListScreen
    }

    fun showWidgetGuideDialog(show: Boolean) {
        _showWidgetGuide.value = show
    }

    fun createNewNote(color: StickyColor = StickyColor.YELLOW, isChecklist: Boolean = false) {
        viewModelScope.launch {
            val starterChecklist = if (isChecklist) {
                listOf(
                    ChecklistItem(text = "Task 1", isChecked = false),
                    ChecklistItem(text = "Task 2", isChecked = false)
                )
            } else {
                emptyList()
            }
            val newNote = StickyNote(
                title = if (isChecklist) "My Checklist" else "New Note",
                content = "",
                colorName = color.name,
                isPinned = false,
                isChecklist = isChecklist,
                checklistJson = ChecklistItem.toJsonArrayString(starterChecklist),
                updatedAt = System.currentTimeMillis()
            )
            val newId = repository.insertOrUpdate(newNote)
            _currentScreen.value = Screen.EditScreen(newId)
        }
    }

    fun saveNote(note: StickyNote) {
        viewModelScope.launch {
            val updated = note.copy(updatedAt = System.currentTimeMillis())
            repository.insertOrUpdate(updated)
            // Synchronize with any active widgets on home screen
            StickyNoteWidgetProvider.updateWidgetsForNote(getApplication(), updated.id)
        }
    }

    fun deleteNote(note: StickyNote) {
        viewModelScope.launch {
            repository.delete(note)
            StickyNoteWidgetProvider.updateAllWidgets(getApplication())
            if (_currentScreen.value is Screen.EditScreen) {
                _currentScreen.value = Screen.ListScreen
            }
        }
    }

    fun togglePin(note: StickyNote) {
        viewModelScope.launch {
            val updated = note.copy(
                isPinned = !note.isPinned,
                updatedAt = System.currentTimeMillis()
            )
            repository.insertOrUpdate(updated)
            StickyNoteWidgetProvider.updateWidgetsForNote(getApplication(), updated.id)
        }
    }

    fun duplicateNote(note: StickyNote) {
        viewModelScope.launch {
            val duplicate = note.copy(
                id = 0,
                title = "${note.title} (Copy)",
                updatedAt = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis()
            )
            repository.insertOrUpdate(duplicate)
            Toast.makeText(getApplication(), "Sticky Note duplicated", Toast.LENGTH_SHORT).show()
        }
    }

    fun toggleChecklistItem(note: StickyNote, itemId: String) {
        val updatedItems = note.checklistItems.map {
            if (it.id == itemId) it.copy(isChecked = !it.isChecked) else it
        }
        val updatedNote = note.copy(
            checklistJson = ChecklistItem.toJsonArrayString(updatedItems),
            updatedAt = System.currentTimeMillis()
        )
        saveNote(updatedNote)
    }

    fun addChecklistItem(note: StickyNote, text: String) {
        if (text.isBlank()) return
        val current = note.checklistItems.toMutableList()
        current.add(ChecklistItem(text = text.trim(), isChecked = false))
        val updatedNote = note.copy(
            checklistJson = ChecklistItem.toJsonArrayString(current),
            updatedAt = System.currentTimeMillis()
        )
        saveNote(updatedNote)
    }

    fun removeChecklistItem(note: StickyNote, itemId: String) {
        val current = note.checklistItems.filterNot { it.id == itemId }
        val updatedNote = note.copy(
            checklistJson = ChecklistItem.toJsonArrayString(current),
            updatedAt = System.currentTimeMillis()
        )
        saveNote(updatedNote)
    }

    fun pinNoteToWidget(note: StickyNote) {
        val context = getApplication<Application>()
        if (StickyNoteWidgetProvider.isPinSupported(context)) {
            val initiated = StickyNoteWidgetProvider.requestPinNote(context, note.id)
            if (!initiated) {
                Toast.makeText(
                    context,
                    "To add widget: Long press on Home Screen -> Widgets -> Sticky Notes",
                    Toast.LENGTH_LONG
                ).show()
                _showWidgetGuide.value = true
            }
        } else {
            Toast.makeText(
                context,
                "To add widget: Long press on Home Screen -> Widgets -> Sticky Notes",
                Toast.LENGTH_LONG
            ).show()
            _showWidgetGuide.value = true
        }
    }
}
