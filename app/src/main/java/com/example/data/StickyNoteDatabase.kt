package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [StickyNote::class], version = 1, exportSchema = false)
abstract class StickyNoteDatabase : RoomDatabase() {
    abstract fun stickyNoteDao(): StickyNoteDao

    companion object {
        @Volatile
        private var INSTANCE: StickyNoteDatabase? = null

        fun getDatabase(context: Context): StickyNoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StickyNoteDatabase::class.java,
                    "sticky_notes_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate starter notes
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialNotes(getDatabase(context).stickyNoteDao())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialNotes(dao: StickyNoteDao) {
            val starterChecklist = listOf(
                ChecklistItem(text = "Add Sticky Note widget to home screen", isChecked = false),
                ChecklistItem(text = "Try different pastel note colors", isChecked = true),
                ChecklistItem(text = "Pin important notes to the top", isChecked = false),
                ChecklistItem(text = "Edit note directly by tapping its widget", isChecked = false)
            )

            val notes = listOf(
                StickyNote(
                    title = "Welcome to Sticky Notes! 📌",
                    content = "Keep your ideas, reminders, and daily thoughts pinned right on your phone.\n\n• Tap any note to edit or change its color\n• Tap the widget icon to pin it to your Home Screen!",
                    colorName = StickyColor.YELLOW.name,
                    isPinned = true,
                    isChecklist = false
                ),
                StickyNote(
                    title = "Getting Started Checklist 📋",
                    content = "",
                    colorName = StickyColor.GREEN.name,
                    isPinned = false,
                    isChecklist = true,
                    checklistJson = ChecklistItem.toJsonArrayString(starterChecklist)
                ),
                StickyNote(
                    title = "Home Screen Widget 📱",
                    content = "Want your note visible all the time?\n\n1. Tap 'Pin to Home' on this note.\n2. Or touch & hold your home screen, choose Widgets, and drag Sticky Note onto your screen!",
                    colorName = StickyColor.BLUE.name,
                    isPinned = false,
                    isChecklist = false
                ),
                StickyNote(
                    title = "Creative Ideas 💡",
                    content = "• Meeting prep & quick phone numbers\n• Morning affirmations\n• Daily focus goal: Ship high quality software!",
                    colorName = StickyColor.PINK.name,
                    isPinned = false,
                    isChecklist = false
                )
            )

            for (note in notes) {
                dao.insert(note)
            }
        }
    }
}
