package com.epaperlauncher.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Icon cache entity - stores metadata about processed icons
 */
@Entity(tableName = "icon_cache")
data class IconCacheEntity(
    @PrimaryKey val packageName: String,
    val filePath: String,
    val processedVersionCode: Int, // Regenerate if app updated & algorithm version changed
    val algorithmVersion: Int
)
