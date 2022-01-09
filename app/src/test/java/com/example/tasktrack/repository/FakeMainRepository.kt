package com.example.tasktrack.repository

import androidx.lifecycle.MutableLiveData
import com.example.tasktrack.database.Track

class FakeMainRepository  {

    private val tracks = mutableListOf<Track>()
    private val observabletrack = MutableLiveData<List<Track>>(tracks)

    fun insertTrack(track: Track){}
}