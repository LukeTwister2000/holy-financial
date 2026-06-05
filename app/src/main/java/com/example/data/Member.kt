package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val contact: String = "",
    val groupName: String = "Geral",
    val age: Int = 0,
    val gender: String = "Masculino",
    val isLeader: Boolean = false,
    val birthDate: Long = 0L,
    val joinedDate: Long = System.currentTimeMillis()
)
