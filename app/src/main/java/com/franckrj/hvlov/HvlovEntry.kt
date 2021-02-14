package com.franckrj.hvlov

/**
 * Class representing an entry returned from the HVlov server.
 */
sealed class HvlovEntry {
    /**
     * A folder entry on the HVlov server.
     *
     * @property title The title of the folder.
     * @property path The path to the folder.
     */
    data class Folder(val title: String, val path: String) : HvlovEntry()

    /**
     * A video entry on the HVlov server.
     *
     * @property title The title of the video.
     * @property relativeUrl The url to access the video, relatively to the URL of the server.
     */
    data class Video(val title: String, val relativeUrl: String) : HvlovEntry()

    /**
     * A group of video on the HVlov server. These group are often formed when the same video can be read with
     * multiple configurations (hd, sd, etc).
     *
     * @property title The title of the group of videos.
     * @property relativeUrlPerTags A map that map a tag (ex: hd) to a relative URL on the server.
     */
    data class VideoGroup(val title: String, val relativeUrlPerTags: Map<String, String>) : HvlovEntry()
}
