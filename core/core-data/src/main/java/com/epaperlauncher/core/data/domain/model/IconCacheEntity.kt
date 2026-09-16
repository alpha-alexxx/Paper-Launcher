package com.epaperlauncher.core.data.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for icon cache metadata.
 */
@Entity(tableName = "icon_cache")
data class IconCacheEntity(
    @PrimaryKey val packageName: String,
    val filePath: String,
    val processedVersionCode: Int,    // regenerate if app updated & algorithm version changed
    val algorithmVersion: Int,
    val iconStyle: String = "flat_monochrome" // flat_monochrome, line_art, stamp
)
