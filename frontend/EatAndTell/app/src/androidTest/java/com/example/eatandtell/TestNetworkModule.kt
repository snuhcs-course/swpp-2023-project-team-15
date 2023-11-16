package com.example.eatandtell

import com.example.eatandtell.data.api.ApiService
import com.example.eatandtell.di.NetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
object TestNetworkModule {

    @Singleton
    @Provides
    fun provideTestApiService(): ApiService {
        return mockk<ApiService>()
    }
}