package com.moma.app.di

import android.content.Context
import androidx.room.Room
import com.moma.app.BuildConfig
import com.moma.core.database.BookingDao
import com.moma.core.database.CoachDao
import com.moma.core.database.GymDao
import com.moma.core.database.MomaDatabase
import com.moma.core.network.MomaApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MomaDatabase =
        Room.databaseBuilder(context, MomaDatabase::class.java, MomaDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideGymDao(db: MomaDatabase): GymDao = db.gymDao()

    @Provides
    fun provideCoachDao(db: MomaDatabase): CoachDao = db.coachDao()

    @Provides
    fun provideBookingDao(db: MomaDatabase): BookingDao = db.bookingDao()

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideMomaApi(client: OkHttpClient): MomaApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.SUPABASE_URL.trimEnd('/') + "/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MomaApi::class.java)
}
