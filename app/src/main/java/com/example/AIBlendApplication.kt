package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.SettingsDataStore
import com.example.data.remote.GeminiApiService
import com.example.data.repository.ArtworkRepositoryImpl
import com.example.domain.repository.ArtworkRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class AIBlendApplication : Application() {
    
    lateinit var database: AppDatabase
    lateinit var settingsDataStore: SettingsDataStore
    lateinit var artworkRepository: ArtworkRepository

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize Room Local Database
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "aiblend_db"
        )
        .fallbackToDestructiveMigration()
        .build()

        // 2. Initialize DataStore
        settingsDataStore = SettingsDataStore(this)

        // 3. Initialize Moshi Parser
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        // 4. Configure OkHttp with generous timeouts for generation
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

        // 5. Build Retrofit client
        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val apiService = retrofit.create(GeminiApiService::class.java)

        // 6. Read gemini keys from BuildConfig
        val defaultApiKey = BuildConfig.GEMINI_API_KEY ?: ""

        // 7. Inject dependencies safely via constructor injection
        artworkRepository = ArtworkRepositoryImpl(
            context = this,
            artworkDao = database.artworkDao(),
            apiService = apiService,
            defaultApiKey = defaultApiKey
        )
    }
}
