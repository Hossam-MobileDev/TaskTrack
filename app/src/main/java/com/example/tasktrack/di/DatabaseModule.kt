package com.example.tasktrack.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.tasktrack.constants.Constants
import com.example.tasktrack.constants.Constants.Companion.DATABASE_NAME
import com.example.tasktrack.database.AppDatabase
import com.example.tasktrack.database.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder( app,AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTrackDao(appDatabase: AppDatabase): TrackDao {
        return appDatabase.trackDao()
    }

}