package com.generis.com.generis.stream.kafka

enum class StreamTopic(val topicValue:String) {
    CREATE_ASSET("create.asset"),
    UPDATE_ASSET("update.asset"),
    EXPORT_ASSET("export.asset")
}