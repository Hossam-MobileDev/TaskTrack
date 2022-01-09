package com.example.tasktrack.repository

import com.example.tasktrack.database.Track
import com.example.tasktrack.database.TrackDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class MainRepository @Inject constructor(
    private val trackDao: TrackDao
) {

    suspend fun insertTrack(track: Track) = trackDao.insertTrack(track)
    suspend fun deleteTrack(track: Track) = trackDao.deleteTrack(track)

    val repotracks = trackDao.getAllTracksByDate()


}