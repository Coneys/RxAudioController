package github.com.coneey.rxaudiomanager.serviceMediaManager

import android.arch.lifecycle.LifecycleService
import android.content.Intent
import android.media.MediaPlayer
import github.com.coneey.rxaudiomanager.mediaListener.MediaStateResolver
import github.com.coneey.rxaudiomanager.simpleMediaManager.InternalMediaPlayer
import github.com.coneey.rxaudiomanager.simpleMediaManager.MediaServiceCommandEmitter
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject


class PlayerService : LifecycleService() {

    internal val mediaPlayer = MediaPlayer()
    internal val mediaListenerResolver = MediaStateResolver(mediaPlayer)
    internal val mediaManager by lazy { InternalMediaPlayer(mediaPlayer, this, mediaListenerResolver) }
    var mediaServiceDisposable: Disposable? = null

    companion object {
        val listening: Subject<Boolean> = BehaviorSubject.create()
    }

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
                    is ServiceCommand.LoadExternalFileMusic -> {
                        println("WTF STARTING")
                        mediaManager.loadExternalFileMusic(it.filePath, it.attr)
                        println("WTF ENDING")
                    }
                    is ServiceCommand.LoadInternalFileMusic -> mediaManager.loadInternalFileMusic(it.filePath, it.attr)
                    is ServiceCommand.Pause -> mediaManager.pause()
                    is ServiceCommand.Resume -> mediaManager.resume()
                    is ServiceCommand.SeekTo -> mediaManager.seekTo(it.millisecond)
                    is ServiceCommand.Restart -> mediaManager.reset()
                    is ServiceCommand.Start -> mediaManager.start()

                }
            }
            listening.onNext(true)
            mediaListenerResolver.infoSubject.subscribe(MediaServiceCommandEmitter.mediaInfoSubject)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        println("SERVICE TEST - DESTROYING")

        mediaManager.finish()
        mediaListenerResolver.finalize()
        mediaServiceDisposable?.dispose()
        listening.onNext(false)
        println("DESTROY SERVICE")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

}