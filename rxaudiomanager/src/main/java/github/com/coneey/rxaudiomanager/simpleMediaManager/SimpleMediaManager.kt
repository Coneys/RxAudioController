package github.com.coneey.rxaudiomanager.simpleMediaManager

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import github.com.coneey.rxaudiomanager.MediaListenerResolver
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import java.io.FileDescriptor

open class SimpleMediaManager(val player: MediaPlayer, val context: Context,
                              val resolver: MediaListenerResolver = MediaListenerResolver(player)) : MediaManager {


    private val resources = context.resources
    private val mediaSubject: Subject<Pair<AudioAttributes, Any>> = BehaviorSubject.create()
    private var mediaDisposable: Disposable? = null


    init {
        resolver.initialize()
        initializeLifeCycle()
        mediaDisposable = mediaSubject
                .doOnNext { println("SOMETHING ARRIVED ${it.second}") }
                .subscribeBy(
                        onError = { throw it },
                        onNext = { startMusic(it) }
                )
    }


    override fun loadStreamMusic(url: String, attributes: AudioAttributes?) {
        val audioAttributes = AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        mediaSubject.onNext((attributes ?: audioAttributes) to url)
    }

    override fun loadResourceMusic(resourceId: Int, attributes: AudioAttributes?) {
        val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        val assetFileDescriptor = resources.openRawResourceFd(resourceId)
        mediaSubject.onNext((attributes ?: audioAttributes) to assetFileDescriptor)
    }

    fun pause() {
        player.pause()
    }


    fun startMusic(pair: Pair<AudioAttributes, Any>) {

        player.let {
            val dataSource = pair.second
            if (dataSource is String || dataSource is AssetFileDescriptor || dataSource is FileDescriptor) {
                it.reset()
                it.setAudioAttributes(pair.first)

                when (dataSource) {
                    is String -> it.setDataSource(dataSource)
                    is AssetFileDescriptor -> it.setDataSource(dataSource.fileDescriptor, dataSource.startOffset, dataSource.length)
                    is FileDescriptor -> it.setDataSource(dataSource)
                }

                it.prepareAsync()
            }

        }
    }

    fun postWhenPrepared(runnable: (MediaPlayer) -> Unit) = resolver.postWhenPrepared(runnable)

    override fun finish() {
        resolver.finalize()
        mediaDisposable?.dispose()
    }

    fun initializeLifeCycle() {
        if (context is LifecycleOwner) {
            context.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun clear() {
                    println("CALLING FINALIZE")
                    finish()
                }
            })
        }
    }


}