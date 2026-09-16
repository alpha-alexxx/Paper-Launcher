package com.epaperlauncher.core.data.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing an installed application.
 */
@Entity(tableName = "apps")
data class AppEntity(
    @PrimaryKey val packageName: String,
    val label: String,
    val isSystemApp: Boolean,
    val installTime: Long,
    val lastUsedTime: Long,
    val launchCount: Int,
    val monochromeIconPath: String?,   // null until icon-engine processes it
    val isHidden: Boolean = false,
    val isPinned: Boolean = false
)

/**
 * Domain model for app entries exposed to UI layer.
 */
data class AppEntry(
    val packageName: String,
    val label: String,
    val isSystemApp: Boolean,
    val installTime: Long,
    val lastUsedTime: Long,
    val launchCount: Int,
    val iconPath: String?,
    val isHidden: Boolean,
    val isPinned: Boolean
) {
    fun toEntity() = AppEntity(
        packageName = packageName,
        label = label,
        isSystemApp = isSystemApp,
        installTime = installTime,
        lastUsedTime = lastUsedTime,
        launchCount = launchCount,
        monochromeIconPath = iconPath,
        isHidden = isHidden,
        isPinned = isPinned
    )

    companion object {
        fun fromEntity(entity: AppEntity) = AppEntry(
            packageName = entity.packageName,
            label = entity.label,
            isSystemApp = entity.isSystemApp,
            installTime = entity.installTime,
            lastUsedTime = entity.lastUsedTime,
            launchCount = entity.launchCount,
            iconPath = entity.monochromeIconPath,
            isHidden = entity.isHidden,
            isPinned = entity.isPinned
        )
    }
}
