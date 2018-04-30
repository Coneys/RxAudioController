package github.com.coneey.rxaudiomanager.serviceMediaManager

import android.arch.lifecycle.LifecycleService
import android.content.Intent
import android.media.MediaPlayer
import github.com.coneey.rxaudiomanager.mediaListener.MediaStateResolver
import github.com.coneey.rxaudiomanager.simpleMediaManager.InternalMediaPlayer
import github.com.coneey.rxaudiomanager.simpleMediaManager.MediaServiceCommandEmitter
import io.reactivex.disposables.Disposable


class PlayerService : LifecycleService() {

    internal val mediaPlayer = MediaPlayer()
    internal val mediaListenerResolver = MediaStateResolver(mediaPlayer)
    internal val mediaManager by lazy { InternalMediaPlayer(mediaPlayer, this, mediaListenerResolver) }
    var mediaServiceDisposable: Disposable? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        listenForMedia()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun listenForMedia() {
        if (mediaServiceDisposable == null) {
            mediaServiceDisposable = MediaServiceCommandEmitter.commandSubject.subscribe {
                when (it) {
                    is ServiceCommand.Finish -> stopSelf()
                    is ServiceCommand.Stop -> mediaManager.stop()
                    is ServiceCommand.LoadStreamMusic -> mediaManager.loadStreamMusic(it.url, it.attr)
                    is ServiceCommand.LoadResourceMusic -> mediaManager.loadResourceMusic(it.resourceId, it.attr)
                    is ServiceCommand.LoadExternalFileMusic -> mediaManager.loadExternalFileMusic(it.filePath, it.attr)
                    is ServiceCommand.LoadInternalFileMusic -> mediaManager.loadInternalFileMusic(it.filePath, it.attr)
                    is ServiceCommand.Pause -> mediaManager.pause()
                    is ServiceCommand.Resume -> mediaManager.resume()
                    is ServiceCommand.SeekTo -> mediaManager.seekTo(it.millisecond)
                    is ServiceCommand.Restart -> mediaManager.restart()

                }
            }
            mediaListenerResolver.infoSubject.subscribe(MediaServiceCommandEmitter.mediaInfoSubject)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaManager.finish()
        mediaListenerResolver.finalize()
        mediaServiceDisposable?.dispose()
        println("DESTROY SERVICE")
    }


}