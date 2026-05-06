package com.example.lifeeasy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lifeeasy.domain.model.GpaResult

@Entity(tableName = "gpa_results")
data class GpaResultEntity(
    @PrimaryKey val id: String,
    val semesterName: String,
    val gpa: Double,
    val totalCredits: Double,
    val timestamp: Long
)

fun GpaResultEntity.toDomain() = GpaResult(
    id = id,
    semesterName = semesterName,
    gpa = gpa,
    totalCredits = totalCredits,
    timestamp = timestamp
)

fun GpaResult.toEntity() = GpaResultEntity(
    id = id,
    semesterName = semesterName,
    gpa = gpa,
    totalCredits = totalCredits,
    timestamp = timestamp
)
