package com.example.util

import com.example.dao.friend.FriendDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


fun periodicWork() {
    val friendDao: FriendDao = FriendDao
    CoroutineScope(Job()).launch {
        friendDao.deleteBothNull()
    }
}