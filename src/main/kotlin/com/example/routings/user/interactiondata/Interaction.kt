package com.example.routings.user.interactiondata

import com.example.models.ext.InteractionData

interface Interaction {

    suspend fun interactionData(loginUserId: Long): InteractionData

    companion object : Interaction by InteractionImpl()
}