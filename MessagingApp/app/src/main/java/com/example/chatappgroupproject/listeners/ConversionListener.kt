package com.example.chatappgroupproject.listeners

import com.example.chatappgroupproject.models.User

interface ConversionListener {
    fun onConversionClicked(user: User?)
}