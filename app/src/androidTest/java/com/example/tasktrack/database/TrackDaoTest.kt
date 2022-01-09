package com.example.tasktrack.database

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.androidTask.trackingapp.adapters.TrackingAdapter
import com.example.tasktrack.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class TrackDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: TrackDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao =database.trackDao()
    }
    @After
    fun teardown() {
        database.close()
    }


    @Test
    fun insertTrack() = runBlockingTest {
        val track = Track(0,image = null, 0L,
            0f, 0,0L)

        dao.insertTrack(track)

        val ftrack = dao.getAllTracksByDate().getOrAwaitValue()

        assertThat(ftrack).contains(ftrack)
    }
}