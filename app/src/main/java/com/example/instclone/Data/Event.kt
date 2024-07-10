package com.example.instclone.Data

open class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set
    fun getContentOrNull(): T? {
        if(hasBeenHandled) {
            return null
        }else{
            hasBeenHandled = true
            return content
        }
    }
}