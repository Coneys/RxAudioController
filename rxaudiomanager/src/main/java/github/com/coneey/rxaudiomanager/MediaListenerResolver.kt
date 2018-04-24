package github.com.coneey.rxaudiomanager

import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log

typealias MediaRunnable = (MediaPlayer) -> Unit

class MediaListenerResolver(val player: MediaPlayer) : MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {


    val onPreparedRunnables: ArrayList<MediaRunnable> = ArrayList()

    fun initialize() {
        player.let {
            it.setOnPreparedListener(this)
            it.setOnErrorListener(this)
            it.setOnSeekCompleteListener(this)
            it.setOnInfoListener(this)
            it.setOnBufferingUpdateListener(this)
        }


    }

    fun finalize() {
        onPreparedRunnables.clear()
        player.release()
    }


    override fun onPrepared(mp: MediaPlayer) {
        mp.start()
        onPreparedRunnables.forEach { it.invoke(mp) }
        onPreparedRunnables.clear()
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK $extra")
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED $extra")
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN $extra")
        }
        return false
    }

    override fun onSeekComplete(mp: MediaPlayer) {
        println("COMPLETED SEEK")
    }

    override fun onInfo(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        println("INFO!!!")
        return false
    }

    override fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
        println("Buff update")
    }

    override fun onAudioFocusChange(focusChange: Int) {
        println("Focus change")
    }

    fun postWhenPrepared(runnable: MediaRunnable) {
        onPreparedRunnables.add(runnable)
    }


}