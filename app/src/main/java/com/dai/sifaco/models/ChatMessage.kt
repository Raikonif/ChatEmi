package com.dai.sifaco.models

import java.util.*

class ChatMessage(val id: String="", val text: String="", val fromId: String="", val toId: String="", val timestamp: Long=-1, val url: String /* time: Date= Date()*/) {
    constructor() : this("", "", "", "", -1, "")
}