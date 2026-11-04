package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sticky_notes")
data class StickyNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val colorName: String = StickyColor.YELLOW.name,
    val isPinned: Boolean = false,
    val isChecklist: Boolean = false,
    val checklistJson: String = "",
    val fontSize: String = "MEDIUM",
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val color: StickyColor
        get() = StickyColor.fromName(colorName)

    val checklistItems: List<ChecklistItem>
        get() = ChecklistItem.fromJsonArrayString(checklistJson)

    fun getPreviewText(): String {
        return if (isChecklist) {
            val items = checklistItems
            if (items.isEmpty()) {
                "Empty checklist"
            } else {
                items.take(5).joinToString("\n") { item ->
                    (if (item.isChecked) "☑ " else "☐ ") + item.text
                }
            }
        } else {
            content.ifBlank { "No content" }
        }
    }
}
