package com.example.chatappgroupproject.listeners

import com.example.chatappgroupproject.models.User

interface UserListener {
    fun onUserClicked(user: User?)
}