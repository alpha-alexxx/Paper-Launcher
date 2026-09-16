package com.epaperlauncher.core.data.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.epaperlauncher.core.data.local.IconCacheDao
import com.epaperlauncher.core.data.model.IconCacheEntity

/**
 * Main Room database for EPaper Launcher
 * Contains tables for app list cache and icon cache
 */
@Database(
    entities = [
        IconCacheEntity::class
        // AppEntity will be added when AppListRepository is fully implemented
    ],
    version = 1,
    exportSchema = true
)
abstract class EPaperDatabase : RoomDatabase() {
    
    abstract fun iconCacheDao(): IconCacheDao
    
    // Future: abstract fun appDao(): AppDao
}
