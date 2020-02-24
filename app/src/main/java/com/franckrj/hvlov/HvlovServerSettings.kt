package com.franckrj.hvlov

data class HvlovServerSettings(
    val url: String,
    val password: String,
    val version: Int
) {
    companion object {
        val default = HvlovServerSettings("", "", 0)
    }
}
