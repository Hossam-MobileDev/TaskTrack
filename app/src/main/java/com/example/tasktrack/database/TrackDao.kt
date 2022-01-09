package com.example.tasktrack.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track:Track)

    @Delete
    suspend fun deleteTrack(track: Track)

    @Query("SELECT * FROM tracking_table ORDER BY timestamp DESC")
    fun getAllTracksByDate(): LiveData<List<Track>>

}