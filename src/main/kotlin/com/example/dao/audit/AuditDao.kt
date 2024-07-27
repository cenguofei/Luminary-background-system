package com.example.dao.audit

import com.example.dao.LunimaryDao
import com.example.models.Audit
import com.example.models.tables.Audits

interface AuditDao : LunimaryDao<Audit, Audits> {

    override suspend fun create(data: Audit): Long

    companion object : AuditDao by AuditDaoImpl()
}