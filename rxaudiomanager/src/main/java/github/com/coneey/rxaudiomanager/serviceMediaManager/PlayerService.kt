package github.com.coneey.rxaudiomanager.serviceMediaManager

import android.arch.lifecycle.LifecycleService
import android.content.Intent
import android.media.MediaPlayer
import github.com.coneey.rxaudiomanager.MediaListenerResolver
import github.com.coneey.rxaudiomanager.simpleMediaManager.MediaServiceCommandEmitter
import github.com.coneey.rxaudiomanager.simpleMediaManager.SimpleMediaManager
import io.reactivex.disposables.Disposable


class PlayerService : LifecycleService() {

    internal val mediaPlayer = MediaPlayer()
    internal val mediaListenerResolver = MediaListenerResolver(mediaPlayer)
    internal val mediaManager by lazy { SimpleMediaManager(mediaPlayer, this, mediaListenerResolver) }
    var disposable: Disposable? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        listenForMedia()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun listenForMedia() {

        disposable = MediaServiceCommandEmitter.commandSubject.subscribe {
            println("COMMAND ARRIVED $it")
            when (it) {
                is ServiceCommand.Stop -> stopSelf()
                is ServiceCommand.LoadStreamMusic -> mediaManager.loadStreamMusic(it.url, it.attr)
                is ServiceCommand.LoadResourceMusic -> mediaManager.loadResourceMusic(it.resourceId, it.attr)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaManager.finish()
        mediaListenerResolver.finalize()
        disposable?.dispose()
        println("DESTROY SERVICE")
    }


}