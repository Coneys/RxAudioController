package github.com.coneey.rxaudiomanager.mediaListener

typealias Second = Int
typealias Millisecond = Int
typealias Percent = Int

data class MediaInfo(val state: MediaState, val bufferProgress: Percent,
                     val currentTimeMilis: Millisecond,
                     val currentTime: Second,
                     val duration: Second)
