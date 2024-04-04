package com.example.dao.view.duration

import com.example.dao.LunimaryDao
import com.example.models.ViewDuration
import com.example.models.tables.ViewDurations

interface ViewDurationDao : LunimaryDao<ViewDuration, ViewDurations> {

    override suspend fun create(data: ViewDuration): Long

    override suspend fun update(id: Long, data: ViewDuration)

    suspend fun userDurationsTop10(loginUserId: Long): List<ViewDuration>

    companion object : ViewDurationDao by ViewDurationDaoImpl()
}