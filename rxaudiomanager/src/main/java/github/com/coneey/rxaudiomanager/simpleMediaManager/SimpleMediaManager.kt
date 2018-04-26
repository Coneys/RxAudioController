package github.com.coneey.rxaudiomanager.simpleMediaManager

import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions

class SimpleMediaManager internal constructor(private val context: Context, player: MediaPlayer,
                                              private val internalMediaPlayer: InternalMediaPlayer = InternalMediaPlayer(player, context))
    : MediaManager by internalMediaPlayer {

    override fun loadExternalFileMusic(filePath: String, attributes: AudioAttributes?) {
        if (context is AppCompatActivity) {
            val permisstions = RxPermissions(context)
            permisstions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .take(1)
                    .subscribe { if (it) internalMediaPlayer.loadExternalFileMusic(filePath, attributes) } // FIXME add disposable?
        } else throw RuntimeException("Context has to be AppCompatActivity!")
    }

}