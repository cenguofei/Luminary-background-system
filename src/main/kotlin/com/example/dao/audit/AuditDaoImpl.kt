package com.example.dao.audit

import com.example.models.Audit
import com.example.models.tables.Audits
import com.example.util.dbTransaction
import org.jetbrains.exposed.sql.insert

class AuditDaoImpl : AuditDao {

    override suspend fun create(data: Audit): Long {
        return dbTransaction {
            Audits.insert { state ->
                state[auditorId] = data.auditorId
                state[articleId] = data.articleId
                state[prevState] = data.prevState.name
                state[toState] = data.toState.name
                state[timestamp] = data.timestamp
            }[Audits.id]
        }
    }
}