package com.example.data

import kotlinx.coroutines.flow.Flow

class ChurchRepository(private val churchDao: ChurchDao) {
    val allMembers: Flow<List<Member>> = churchDao.getAllMembers()
    val allTransactions: Flow<List<Transaction>> = churchDao.getAllTransactions()
    val allAuditLogs: Flow<List<AuditLog>> = churchDao.getAllAuditLogs()

    suspend fun insertMember(member: Member) = churchDao.insertMember(member)
    suspend fun insertTransaction(transaction: Transaction) = churchDao.insertTransaction(transaction)
    suspend fun deleteTransaction(id: Int) = churchDao.deleteTransaction(id)
    suspend fun insertAuditLog(log: AuditLog) = churchDao.insertAuditLog(log)
}
