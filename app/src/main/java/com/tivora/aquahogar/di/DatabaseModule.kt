package com.tivora.aquahogar.di

import android.content.Context
import androidx.room.Room
import com.tivora.aquahogar.data.local.AquaHogarDatabase
import com.tivora.aquahogar.data.local.dao.AlertaConsumoDao
import com.tivora.aquahogar.data.local.dao.RegistroConsumoDao
import com.tivora.aquahogar.data.local.dao.ViviendaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AquaHogarDatabase =
        Room.databaseBuilder(
            context,
            AquaHogarDatabase::class.java,
            "aquahogar.db"
        ).fallbackToDestructiveMigration(true).build()

    @Provides
    fun provideViviendaDao(db: AquaHogarDatabase): ViviendaDao = db.viviendaDao()

    @Provides
    fun provideRegistroConsumoDao(db: AquaHogarDatabase): RegistroConsumoDao = db.registroConsumoDao()

    @Provides
    fun provideAlertaConsumoDao(db: AquaHogarDatabase): AlertaConsumoDao = db.alertaConsumoDao()
}
