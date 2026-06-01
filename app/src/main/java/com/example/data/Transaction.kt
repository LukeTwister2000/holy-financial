package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    DIZIMO, OFERTA, DESPESA
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val date: Long = System.currentTimeMillis(),
    val memberId: Int? = null,
    val isPaidViaPixOrCard: Boolean = false // Simulation of PIX/Card integration
)
