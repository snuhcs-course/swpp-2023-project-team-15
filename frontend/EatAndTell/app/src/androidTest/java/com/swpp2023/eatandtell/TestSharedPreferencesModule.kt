package com.swpp2023.eatandtell

import com.swpp2023.eatandtell.data.security.SharedPreferencesManager
import com.swpp2023.eatandtell.data.security.SharedPreferencesManager.getToken
import com.swpp2023.eatandtell.di.NetworkModule
import com.swpp2023.eatandtell.di.SharedPreferencesModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.every
import io.mockk.mockk
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SharedPreferencesModule::class]
)
object TestSharedPreferencesModule {
    @Provides
    @Singleton
    fun provideTestSharedPreferenceManager(): SharedPreferencesManager {
        return mockk {
            every { getToken(any()) } returns mapOf("Token" to "da8c255f281e2b7ba54600c0a41ff02892787b96")
            every { setToken(any(), any()) } returns Unit
        }
    }
}