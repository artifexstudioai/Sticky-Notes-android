package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.StickyNote
import com.example.data.StickyNoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StickyNoteWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (appWidgetId in appWidgetIds) {
                    updateAppWidgetInternal(context, appWidgetManager, appWidgetId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in onUpdate", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            WidgetPreferences.deleteWidgetPref(context, appWidgetId)
        }
    }

    companion object {
        private const val TAG = "StickyNoteWidget"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    updateAppWidgetInternal(context, appWidgetManager, appWidgetId)
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating widget $appWidgetId", e)
                }
            }
        }

        suspend fun updateAppWidgetInternal(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            try {
                val noteId = WidgetPreferences.getWidgetNoteId(context, appWidgetId)
                val db = StickyNoteDatabase.getDatabase(context)
                val note = if (noteId != -1L) {
                    db.stickyNoteDao().getNoteById(noteId)
                } else {
                    val latest = db.stickyNoteDao().getLatestNote()
                    if (latest != null) {
                        WidgetPreferences.saveWidgetNoteId(context, appWidgetId, latest.id)
                    }
                    latest
                }

                val views = buildRemoteViews(context, appWidgetId, note)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update widget $appWidgetId", e)
                // Safe fallback to prevent "Can't load widget"
                val fallbackViews = buildFallbackViews(context, appWidgetId)
                appWidgetManager.updateAppWidget(appWidgetId, fallbackViews)
            }
        }

        fun buildRemoteViews(context: Context, appWidgetId: Int, note: StickyNote?): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_sticky_note)

            if (note != null) {
                // Set background resource on the ImageView background (RemoteViews fully supports setImageViewResource)
                views.setImageViewResource(R.id.widget_background, note.color.widgetBgDrawable)

                val titleColor = if (note.color.isDark) Color.WHITE else 0xFF1F2937.toInt()
                val contentColor = if (note.color.isDark) 0xFFECEFF1.toInt() else 0xFF374151.toInt()
                val dateColor = if (note.color.isDark) 0xFFB0BEC5.toInt() else 0xFF6B7280.toInt()

                views.setTextColor(R.id.widget_title, titleColor)
                views.setTextColor(R.id.widget_content, contentColor)
                views.setTextColor(R.id.widget_date, dateColor)

                views.setTextViewText(R.id.widget_title, note.title.ifBlank { "Sticky Note" })
                views.setTextViewText(R.id.widget_content, note.getPreviewText())

                val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                views.setTextViewText(R.id.widget_date, sdf.format(Date(note.updatedAt)))
                views.setViewVisibility(R.id.widget_pin_icon, if (note.isPinned) View.VISIBLE else View.GONE)

                // Intent to open note in MainActivity
                val clickIntent = Intent(context, MainActivity::class.java).apply {
                    putExtra(MainActivity.EXTRA_NOTE_ID, note.id)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
                views.setOnClickPendingIntent(R.id.widget_edit_btn, pendingIntent)
            } else {
                // Empty state
                views.setImageViewResource(R.id.widget_background, R.drawable.widget_bg_yellow)
                views.setTextViewText(R.id.widget_title, "Sticky Note")
                views.setTextViewText(R.id.widget_content, "Tap here to select or write a note for your home screen!")
                views.setTextViewText(R.id.widget_date, "Tap to configure")
                views.setViewVisibility(R.id.widget_pin_icon, View.GONE)

                val configIntent = Intent(context, StickyNoteWidgetConfigureActivity::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    configIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            }

            return views
        }

        private fun buildFallbackViews(context: Context, appWidgetId: Int): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_sticky_note)
            views.setImageViewResource(R.id.widget_background, R.drawable.widget_bg_yellow)
            views.setTextViewText(R.id.widget_title, "Sticky Note")
            views.setTextViewText(R.id.widget_content, "Tap to open Sticky Notes app.")
            views.setTextViewText(R.id.widget_date, "Sticky Notes")
            views.setViewVisibility(R.id.widget_pin_icon, View.GONE)

            val clickIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            return views
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, StickyNoteWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            CoroutineScope(Dispatchers.IO).launch {
                for (id in allWidgetIds) {
                    updateAppWidgetInternal(context, appWidgetManager, id)
                }
            }
        }

        fun updateWidgetsForNote(context: Context, noteId: Long) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val boundIds = WidgetPreferences.getWidgetIdsForNote(context, noteId)
            CoroutineScope(Dispatchers.IO).launch {
                for (id in boundIds) {
                    updateAppWidgetInternal(context, appWidgetManager, id)
                }
            }
        }

        fun isPinSupported(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                appWidgetManager.isRequestPinAppWidgetSupported
            } else {
                false
            }
        }

        fun requestPinNote(context: Context, noteId: Long): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                if (appWidgetManager.isRequestPinAppWidgetSupported) {
                    val myProvider = ComponentName(context, StickyNoteWidgetProvider::class.java)

                    val successCallback = Intent(context, PinWidgetCallbackReceiver::class.java).apply {
                        putExtra(MainActivity.EXTRA_NOTE_ID, noteId)
                    }
                    val successPendingIntent = PendingIntent.getBroadcast(
                        context,
                        noteId.toInt(),
                        successCallback,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    )

                    return appWidgetManager.requestPinAppWidget(myProvider, null, successPendingIntent)
                }
            }
            return false
        }
    }
}
