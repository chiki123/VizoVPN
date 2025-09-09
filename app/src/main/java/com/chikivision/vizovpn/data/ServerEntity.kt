package com.chikivision.vizovpn.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val country: String,
    val countryCode: String, // For the flag emoji
    val isPremium: Boolean,
    val config: String // The full WireGuard configuration string
)