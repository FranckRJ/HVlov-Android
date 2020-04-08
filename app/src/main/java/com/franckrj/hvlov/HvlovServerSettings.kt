package com.franckrj.hvlov

// TODO: Struct contains to much things, it's hard to tell what property is dependent on what (server or client). Move client configuration to a specific struct and add it to this one.

/**
 * The settings for an HVlov server, used to access it or for client-specific configuration.
 *
 * @property url The URL to access the server.
 * @property password The password to access the server.
 * @property version The version of the client.
 */
data class HvlovServerSettings(
    val url: String,
    val password: String,
    val version: Int
) {
    companion object {
        val default = HvlovServerSettings("", "", 0)
    }
}
