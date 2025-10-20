package com.example.chatappgroupproject.models

import java.util.Date

class ChatMessage {
    @JvmField
    var senderId: String? = null
    @JvmField
    var receiverId: String? = null
    @JvmField
    var message: String? = null
    @JvmField
    var dateTime: String? = null
    @JvmField
    var dateObject: Date? = null
    @JvmField
    var conversationId: String? = null
    @JvmField
    var conversationName: String? = null
    @JvmField
    var conversationImage: String? = null
}