package github.com.coneey.rxaudiomanager.simpleMediaManager

import android.media.AudioAttributes
import github.com.coneey.rxaudiomanager.mediaListener.MediaInfo
import github.com.coneey.rxaudiomanager.mediaListener.Millisecond
import io.reactivex.Observable

interface MediaManager {

    fun loadStreamMusic(url: String, attributes: AudioAttributes? = null)
    fun loadResourceMusic(resourceId: Int, attributes: AudioAttributes? = null)
    fun loadExternalFileMusic(filePath: String, attributes: AudioAttributes? = null)
    fun loadInternalFileMusic(filePath: String, attributes: AudioAttributes? = null)
    fun seekTo(millisecond: Millisecond)
    fun finish()
    fun pause()
    fun resume()
    fun stop()
    fun restart()
    fun getMediaInfoObservable(): Observable<MediaInfo>

}