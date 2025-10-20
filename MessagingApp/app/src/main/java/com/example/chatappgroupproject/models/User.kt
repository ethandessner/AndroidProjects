package com.example.chatappgroupproject.models

import java.io.Serializable

class User : Serializable {
    lateinit var name : String
    lateinit var image : String
    lateinit var email: String
    lateinit var token: String
    lateinit var id: String
}