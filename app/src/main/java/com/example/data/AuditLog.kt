package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val userName: String = "Admin"
)
