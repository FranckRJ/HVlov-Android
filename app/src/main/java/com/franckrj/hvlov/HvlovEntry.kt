package com.franckrj.hvlov

data class HvlovEntry(
    val title: String,
    val url: String,
    val type: Type
) {
    enum class Type {
        VIDEO,
        FOLDER
    }
}
