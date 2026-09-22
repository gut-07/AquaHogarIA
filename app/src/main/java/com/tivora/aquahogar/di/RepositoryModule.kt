package com.tivora.aquahogar.di

import com.tivora.aquahogar.data.auth.AuthRepositoryImpl
import com.tivora.aquahogar.data.repository.ConsumoRepositoryImpl
import com.tivora.aquahogar.domain.repository.AuthRepository
import com.tivora.aquahogar.domain.repository.ConsumoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindConsumoRepository(impl: ConsumoRepositoryImpl): ConsumoRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
