package com.yyj.frecord

import java.io.Serializable

data class MessageData (var id: Int, var date: Long, var content: String, var checked: Boolean) : Serializable