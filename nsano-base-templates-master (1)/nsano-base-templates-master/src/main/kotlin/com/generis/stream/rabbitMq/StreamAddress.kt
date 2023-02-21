package com.generis.com.generis.stream.rabbitMq

enum class StreamAddress(val addressValue:String) {
    CREATE_ASSET("create.asset"),
    UPDATE_ASSET("update.asset"),
    EXPORT_ASSET("export.asset")
}