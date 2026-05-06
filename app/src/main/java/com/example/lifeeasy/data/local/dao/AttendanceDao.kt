package com.example.lifeeasy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE subjectId = :subjectId ORDER BY date DESC")
    fun getAttendanceBySubject(subjectId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE date >= :startDate AND date < :endDate LIMIT 1")
    fun getAttendanceForDateRange(startDate: Long, endDate: Long): Flow<AttendanceEntity?>

    @Query("SELECT * FROM attendance WHERE id = :id")
    suspend fun getAttendanceById(id: String): AttendanceEntity?

    @Query("SELECT * FROM attendance WHERE syncStatus != :status")
    suspend fun getUnsyncedAttendance(status: SyncStatus = SyncStatus.SYNCED): List<AttendanceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: AttendanceEntity)

    @Update
    suspend fun update(attendance: AttendanceEntity)

    @Delete
    suspend fun delete(attendance: AttendanceEntity)

    @Query("UPDATE attendance SET syncStatus = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long = System.currentTimeMillis())
}
