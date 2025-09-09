package com.chikivision.vizovpn.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chikivision.vizovpn.data.AppDatabase
import com.chikivision.vizovpn.data.ServerDao
import com.chikivision.vizovpn.data.ServerEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        serverDaoProvider: Provider<ServerDao>
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "vizovpn_database"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Pre-populate the database on first creation
                    CoroutineScope(Dispatchers.IO).launch {
                        prePopulateServers(serverDaoProvider.get())
                    }
                }
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideServerDao(appDatabase: AppDatabase): ServerDao {
        return appDatabase.serverDao()
    }

    private suspend fun prePopulateServers(serverDao: ServerDao) {
        val servers = listOf(
            ServerEntity(
                country = "United States",
                countryCode = "US",
                isPremium = false,
                // ✅ Real keys have been added here
                config = """
                    [Interface]
                    PrivateKey = IHH1ic7LXiI2hZ4sOqOHs7qYyAvCV+I+ZP1MVMpTkHo=
                    Address = 10.0.0.2/32
                    DNS = 1.1.1.1
                    [Peer]
                    PublicKey = Dx1XCHTq3mQBX8/NJlO3LcRPOLOIJjG33BckjgZkyRk=
                    PresharedKey = Ik0FkjZ7OvOU/gr/2oGvuj61fOLq0+FsdKRWGb5DFpw=
                    AllowedIPs = 0.0.0.0/0
                    Endpoint = YOUR_SERVER_IP:PORT 
                """.trimIndent()
            ),
            ServerEntity(
                country = "Germany",
                countryCode = "DE",
                isPremium = false,
                config = """
                    [Interface]
                    PrivateKey = CLIENT_PRIVATE_KEY_DE
                    Address = 10.0.0.3/32
                    DNS = 1.1.1.1
                    [Peer]
                    PublicKey = SERVER_PUBLIC_KEY_DE
                    AllowedIPs = 0.0.0.0/0
                    Endpoint = de.server.com:51820
                """.trimIndent()
            ),
            ServerEntity(
                country = "Japan",
                countryCode = "JP",
                isPremium = true,
                config = """
                    [Interface]
                    PrivateKey = CLIENT_PRIVATE_KEY_JP
                    Address = 10.0.0.4/32
                    DNS = 1.1.1.1
                    [Peer]
                    PublicKey = SERVER_PUBLIC_KEY_JP
                    AllowedIPs = 0.0.0.0/0
                    Endpoint = jp.server.com:51820
                """.trimIndent()
            )
        )
        serverDao.insertAll(servers)
    }
}