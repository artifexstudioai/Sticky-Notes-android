package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.R

enum class StickyColor(
    val displayName: String,
    val bgHex: String,
    val composeBg: Color,
    val composeOnBg: Color,
    val composeAccent: Color,
    val widgetBgDrawable: Int,
    val isDark: Boolean = false
) {
    YELLOW(
        displayName = "Canary Yellow",
        bgHex = "#FFF9C4",
        composeBg = Color(0xFFFFF9C4),
        composeOnBg = Color(0xFF263238),
        composeAccent = Color(0xFFFBC02D),
        widgetBgDrawable = R.drawable.widget_bg_yellow
    ),
    PINK(
        displayName = "Coral Pink",
        bgHex = "#FFCDD2",
        composeBg = Color(0xFFFFCDD2),
        composeOnBg = Color(0xFF263238),
        composeAccent = Color(0xFFE57373),
        widgetBgDrawable = R.drawable.widget_bg_pink
    ),
    GREEN(
        displayName = "Mint Green",
        bgHex = "#C8E6C9",
        composeBg = Color(0xFFC8E6C9),
        composeOnBg = Color(0xFF1B5E20),
        composeAccent = Color(0xFF81C784),
        widgetBgDrawable = R.drawable.widget_bg_green
    ),
    BLUE(
        displayName = "Sky Blue",
        bgHex = "#BBDEFB",
        composeBg = Color(0xFFBBDEFB),
        composeOnBg = Color(0xFF0D47A1),
        composeAccent = Color(0xFF64B5F6),
        widgetBgDrawable = R.drawable.widget_bg_blue
    ),
    PURPLE(
        displayName = "Lavender",
        bgHex = "#E1BEE7",
        composeBg = Color(0xFFE1BEE7),
        composeOnBg = Color(0xFF4A148C),
        composeAccent = Color(0xFFBA68C8),
        widgetBgDrawable = R.drawable.widget_bg_purple
    ),
    ORANGE(
        displayName = "Peach",
        bgHex = "#FFE0B2",
        composeBg = Color(0xFFFFE0B2),
        composeOnBg = Color(0xFFE65100),
        composeAccent = Color(0xFFFFB74D),
        widgetBgDrawable = R.drawable.widget_bg_orange
    ),
    CHARCOAL(
        displayName = "Midnight Slate",
        bgHex = "#37474F",
        composeBg = Color(0xFF37474F),
        composeOnBg = Color(0xFFECEFF1),
        composeAccent = Color(0xFF90A4AE),
        widgetBgDrawable = R.drawable.widget_bg_charcoal,
        isDark = true
    );

    companion object {
        fun fromName(name: String?): StickyColor {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: YELLOW
        }
    }
}
