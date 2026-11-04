package com.example.data

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isChecked: Boolean = false
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("text", text)
            put("isChecked", isChecked)
        }
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): ChecklistItem {
            return ChecklistItem(
                id = obj.optString("id", UUID.randomUUID().toString()),
                text = obj.optString("text", ""),
                isChecked = obj.optBoolean("isChecked", false)
            )
        }

        fun toJsonArrayString(items: List<ChecklistItem>): String {
            val jsonArray = JSONArray()
            for (item in items) {
                jsonArray.put(item.toJsonObject())
            }
            return jsonArray.toString()
        }

        fun fromJsonArrayString(jsonStr: String?): List<ChecklistItem> {
            if (jsonStr.isNullOrBlank()) return emptyList()
            return try {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<ChecklistItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(fromJsonObject(obj))
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
