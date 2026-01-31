package com.example.remeducp2.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_log")
data class AuditLog(
    @PrimaryKey(autoGenerate = true)
    val idLog: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val tableName: String,
    val recordId: String,
    val actionType: String,
    val oldData: String? = null,
    val newData: String? = null
)
