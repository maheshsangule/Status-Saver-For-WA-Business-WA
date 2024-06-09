package com.developermaheshapps.statussaver.data

import java.io.Serializable

// MediaItem.kt
data class SavedItem(val filePath: String, val type: MediaType):Serializable

enum class MediaType {
    VIDEO,
    PHOTO
}
