package com.example.util

import com.example.dao.friend.FriendDao
import com.example.dao.friend.FriendDaoImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


fun periodicWork() {
    val friendDao: FriendDao = FriendDaoImpl()
    CoroutineScope(Job()).launch {
        friendDao.deleteBothNull()
    }
}