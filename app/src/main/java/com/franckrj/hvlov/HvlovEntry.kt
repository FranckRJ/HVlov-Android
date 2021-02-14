package com.franckrj.hvlov

/**
 * Class representing an entry returned from the HVlov server.
 */
sealed class HvlovEntry {
    /**
     * A video entry on the HVlov server.
     *
     * @property title The title of the video.
     * @property url The url to access the video.
     */
    data class Video(val title: String, val url: String) : HvlovEntry()

    /**
     * A folder entry on the HVlov server.
     *
     * @property title The title of the folder.
     * @property path The path to the folder.
     */
    data class Folder(val title: String, val path: String) : HvlovEntry()
}
