package com.example.tasktrack.ui.viewmodels

import androidx.lifecycle.*
import com.example.tasktrack.database.Track
import com.example.tasktrack.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject internal constructor(
    val mainRepository: MainRepository

) : ViewModel() {

    fun insertTrack(track: Track) = viewModelScope.launch {
        mainRepository.insertTrack(track)
    }

    val tracks: LiveData<List<Track>> = mainRepository.repotracks

}