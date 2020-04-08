package com.franckrj.hvlov

/**
 * Class representing an entry returned from the HVlov server.
 *
 * @property title The title of the entry.
 * @property url The URL to access the entry.
 * @property type The type of entry, either VIDEO or FOLDER.
 */
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
