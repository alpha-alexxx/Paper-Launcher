package com.epaperlauncher.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.epaperlauncher.core.data.model.IconCacheEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for icon cache operations
 */
@Dao
interface IconCacheDao {
    
    @Query("SELECT * FROM icon_cache WHERE packageName = :packageName")
    suspend fun getIconCache(packageName: String): IconCacheEntity?
    
    @Query("SELECT * FROM icon_cache WHERE packageName = :packageName")
    fun observeIconPath(packageName: String): Flow<IconCacheEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cache: IconCacheEntity)
    
    @Delete
    suspend fun deleteIconCache(cache: IconCacheEntity)
    
    @Query("DELETE FROM icon_cache WHERE packageName = :packageName")
    suspend fun deleteIconCache(packageName: String)
    
    @Query("DELETE FROM icon_cache")
    suspend fun clearAllCache()
    
    @Query("SELECT filePath FROM icon_cache WHERE packageName = :packageName")
    suspend fun getIconFilePath(packageName: String): String?
}
