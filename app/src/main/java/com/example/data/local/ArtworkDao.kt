package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtworkDao {
    @Query("SELECT * FROM artworks ORDER BY timestamp DESC")
    fun getAllArtworks(): Flow<List<ArtworkEntity>>

    @Query("SELECT * FROM artworks WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteArtworks(): Flow<List<ArtworkEntity>>

    @Query("SELECT * FROM artworks WHERE isDownloaded = 1 ORDER BY timestamp DESC")
    fun getDownloadedArtworks(): Flow<List<ArtworkEntity>>

    @Query("SELECT * FROM artworks WHERE prompt LIKE :query OR style LIKE :query ORDER BY timestamp DESC")
    fun searchArtworks(query: String): Flow<List<ArtworkEntity>>

    @Query("SELECT * FROM artworks WHERE id = :id LIMIT 1")
    suspend fun getArtworkById(id: Long): ArtworkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtwork(artwork: ArtworkEntity): Long

    @Update
    suspend fun updateArtwork(artwork: ArtworkEntity)

    @Query("DELETE FROM artworks WHERE id = :id")
    suspend fun deleteArtworkById(id: Long)

    @Query("UPDATE artworks SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE artworks SET isDownloaded = :isDownloaded WHERE id = :id")
    suspend fun updateDownloaded(id: Long, isDownloaded: Boolean)
}
