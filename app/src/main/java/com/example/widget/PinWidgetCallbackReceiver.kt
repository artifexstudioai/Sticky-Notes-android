package com.example.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.MainActivity

class PinWidgetCallbackReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        val noteId = intent.getLongExtra(MainActivity.EXTRA_NOTE_ID, -1L)

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && noteId != -1L) {
            WidgetPreferences.saveWidgetNoteId(context, appWidgetId, noteId)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            StickyNoteWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId)
            Toast.makeText(context, "Sticky Note added to Home Screen!", Toast.LENGTH_SHORT).show()
        }
    }
}
