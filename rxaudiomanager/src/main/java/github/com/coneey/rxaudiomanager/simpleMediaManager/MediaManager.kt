package github.com.coneey.rxaudiomanager.simpleMediaManager

import android.media.AudioAttributes

interface MediaManager {

    fun loadStreamMusic(url: String, attributes: AudioAttributes? = null)
    fun loadResourceMusic(resourceId: Int, attributes: AudioAttributes? = null)
    fun finish()

}