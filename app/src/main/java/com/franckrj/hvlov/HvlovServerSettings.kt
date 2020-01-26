package com.franckrj.hvlov

data class HvlovServerSettings(
    val url: String,
    val password: String
) {
    companion object {
        val default = HvlovServerSettings("", "")
    }
}
