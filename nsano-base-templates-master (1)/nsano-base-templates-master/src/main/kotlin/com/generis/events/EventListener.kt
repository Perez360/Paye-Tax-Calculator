package com.generis.events

interface EventListener {
    fun notify(eventType: EventType, data: Any, headers: Map<String, String>?)
}