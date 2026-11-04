package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StickyNoteDao {
    @Query("SELECT * FROM sticky_notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<StickyNote>>

    @Query("SELECT * FROM sticky_notes WHERE id = :id LIMIT 1")
    fun getNoteFlow(id: Long): Flow<StickyNote?>

    @Query("SELECT * FROM sticky_notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Long): StickyNote?

    @Query("SELECT * FROM sticky_notes ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatestNote(): StickyNote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: StickyNote): Long

    @Update
    suspend fun update(note: StickyNote)

    @Delete
    suspend fun delete(note: StickyNote)

    @Query("DELETE FROM sticky_notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM sticky_notes")
    suspend fun getCount(): Int
}
