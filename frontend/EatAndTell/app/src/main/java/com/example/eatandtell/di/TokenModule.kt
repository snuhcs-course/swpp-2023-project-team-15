package com.example.eatandtell.di
import com.example.eatandtell.data.repository.TokenRepository
import com.example.eatandtell.data.security.SharedPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
@Module
@InstallIn(SingletonComponent::class)
object TokenModule {

    @Provides
    fun provideTokenRepository(sharedPreferencesManager: SharedPreferencesManager): TokenRepository {
        return TokenRepository(sharedPreferencesManager)
    }
}
