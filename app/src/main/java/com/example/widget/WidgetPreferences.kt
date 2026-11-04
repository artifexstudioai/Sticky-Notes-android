package com.example.widget

import android.content.Context
import android.content.SharedPreferences

object WidgetPreferences {
    private const val PREFS_NAME = "com.example.widget.StickyNoteWidget"
    private const val PREF_PREFIX_NOTE = "widget_note_"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveWidgetNoteId(context: Context, appWidgetId: Int, noteId: Long) {
        getPrefs(context).edit().putLong(PREF_PREFIX_NOTE + appWidgetId, noteId).apply()
    }

    fun getWidgetNoteId(context: Context, appWidgetId: Int): Long {
        return getPrefs(context).getLong(PREF_PREFIX_NOTE + appWidgetId, -1L)
    }

    fun deleteWidgetPref(context: Context, appWidgetId: Int) {
        getPrefs(context).edit().remove(PREF_PREFIX_NOTE + appWidgetId).apply()
    }

    fun getWidgetIdsForNote(context: Context, noteId: Long): List<Int> {
        val prefs = getPrefs(context)
        val result = mutableListOf<Int>()
        for ((key, value) in prefs.all) {
            if (key.startsWith(PREF_PREFIX_NOTE) && value == noteId) {
                val widgetIdStr = key.removePrefix(PREF_PREFIX_NOTE)
                widgetIdStr.toIntOrNull()?.let { result.add(it) }
            }
        }
        return result
    }
}
