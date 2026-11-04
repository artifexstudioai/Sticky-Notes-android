package com.example.data

import kotlinx.coroutines.flow.Flow

class StickyNoteRepository(private val dao: StickyNoteDao) {
    val allNotes: Flow<List<StickyNote>> = dao.getAllNotes()

    fun getNoteFlow(id: Long): Flow<StickyNote?> = dao.getNoteFlow(id)

    suspend fun getNoteById(id: Long): StickyNote? = dao.getNoteById(id)

    suspend fun getLatestNote(): StickyNote? = dao.getLatestNote()

    suspend fun insertOrUpdate(note: StickyNote): Long = dao.insert(note)

    suspend fun delete(note: StickyNote) = dao.delete(note)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
