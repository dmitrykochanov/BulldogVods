package com.dmko.bulldogvods.di.modules.features

import android.content.Context
import androidx.room.Room
import com.dmko.bulldogvods.features.vods.data.database.VodsDatabase
import com.dmko.bulldogvods.features.vods.data.database.dao.VodsDao
import com.dmko.bulldogvods.features.vods.data.database.datasource.DatabaseVodsDataSource
import com.dmko.bulldogvods.features.vods.data.database.datasource.RoomDatabaseVodsDataSource
import com.dmko.bulldogvods.features.vods.data.network.datasource.ApolloNetworkVodsDataSource
import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface VodsModule {

    @Binds
    @Singleton
    fun provideNetworkVodsDataSource(apolloNetworkVodsDataSource: ApolloNetworkVodsDataSource): NetworkVodsDataSource

    @Binds
    @Singleton
    fun provideDatabaseVodsDataSource(roomDatabaseVodsDataSource: RoomDatabaseVodsDataSource): DatabaseVodsDataSource

    companion object {

        @Provides
        @Singleton
        fun provideVodsDatabase(@ApplicationContext context: Context): VodsDatabase {
            return Room.databaseBuilder(context, VodsDatabase::class.java, "vods_db")
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
        }

        @Provides
        @Singleton
        fun provideVodsDao(vodsDatabase: VodsDatabase): VodsDao {
            return vodsDatabase.vodsDao()
        }
    }
}
