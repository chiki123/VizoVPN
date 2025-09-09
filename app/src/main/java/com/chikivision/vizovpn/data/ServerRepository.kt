package com.chikivision.vizovpn.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerRepository @Inject constructor(
    private val serverDao: ServerDao
) {

    fun getAllServers(): Flow<List<ServerEntity>> {
        return serverDao.getAllServers()
    }
}