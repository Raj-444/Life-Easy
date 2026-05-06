package com.example.lifeeasy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.lifeeasy.data.local.SyncStatus
import java.util.UUID

@Entity(
    tableName = "counter_history",
    foreignKeys = [
        ForeignKey(
            entity = CounterEntity::class,
            parentColumns = ["id"],
            childColumns = ["counterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("counterId")]
)
data class CounterHistoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val counterId: String,
    val oldCount: Int,
    val newCount: Int,
    val changeType: String, // "INCREMENT", "DECREMENT", "RESET", "SET"
    val timestamp: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
