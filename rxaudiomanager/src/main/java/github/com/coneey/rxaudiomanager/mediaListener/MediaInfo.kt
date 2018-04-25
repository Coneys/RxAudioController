package github.com.coneey.rxaudiomanager.mediaListener

typealias Second = Int
typealias Percent = Int

data class MediaInfo(val state: MediaState, val bufferProgress: Percent, val currentTime: Second)
