package com.example.lifeeasy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lifeeasy.domain.model.RoutineItem

@Entity(tableName = "routine_items")
data class RoutineEntity(
    @PrimaryKey val id: String,
    val dayOfWeek: Int,
    val subjectName: String,
    val startTime: String,
    val endTime: String,
    val room: String,
    val teacher: String
)

fun RoutineEntity.toDomain() = RoutineItem(
    id = id,
    dayOfWeek = dayOfWeek,
    subjectName = subjectName,
    startTime = startTime,
    endTime = endTime,
    room = room,
    teacher = teacher
)

fun RoutineItem.toEntity() = RoutineEntity(
    id = id,
    dayOfWeek = dayOfWeek,
    subjectName = subjectName,
    startTime = startTime,
    endTime = endTime,
    room = room,
    teacher = teacher
)
