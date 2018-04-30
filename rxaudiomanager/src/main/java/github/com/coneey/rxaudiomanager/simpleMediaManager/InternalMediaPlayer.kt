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
import android.os.Environment
import github.com.coneey.rxaudiomanager.mediaListener.MediaInfo
import github.com.coneey.rxaudiomanager.mediaListener.MediaState
import github.com.coneey.rxaudiomanager.mediaListener.MediaStateResolver
import github.com.coneey.rxaudiomanager.mediaListener.Millisecond
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import java.io.FileDescriptor

open class InternalMediaPlayer(val player: MediaPlayer, val context: Context,
                               val resolver: MediaStateResolver = MediaStateResolver(player)) : MediaManager {


    private val resources = context.resources
    private val mediaSubject: Subject<Pair<AudioAttributes, Any>> = BehaviorSubject.create()
    private var mediaDisposable: Disposable? = null

    private var currentDataSource: String? = null
    private var currentAudioAttributes: AudioAttributes? = null

    init {
        resolver.initialize()
        initializeLifeCycle()

        mediaDisposable = mediaSubject
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

    override fun loadExternalFileMusic(filePath: String, attributes: AudioAttributes?) {
        val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        val path = "${Environment.getExternalStorageDirectory()}/$filePath"
        mediaSubject.onNext((attributes ?: audioAttributes) to path)
    }

    override fun loadInternalFileMusic(filePath: String, attributes: AudioAttributes?) {
        val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        val path = "${context.cacheDir}/$filePath"
        mediaSubject.onNext((attributes ?: audioAttributes) to path)
    }

    override fun seekTo(millisecond: Millisecond) {

        resolver.seekTo(millisecond)
    }

    override fun getMediaInfoObservable(): Observable<MediaInfo> = resolver.infoSubject


    override fun pause() {
        resolver.pause()

    }

    override fun resume() {
        resolver.resume()
    }

    override fun stop() {
        resolver.stop()
    }

    override fun finish() {
        resolver.finalize()
        mediaDisposable?.dispose()
    }

    override fun restart() {
        val audioAttr = currentAudioAttributes
        val datasource = currentDataSource
        if (datasource != null && audioAttr != null) {
            mediaSubject.onNext(audioAttr to datasource)
        }
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
                currentDataSource = dataSource.toString()
                currentAudioAttributes = pair.first
                it.prepareAsync()
                resolver.pushState(MediaState.PREPARING)
            }

        }
    }


    private fun initializeLifeCycle() {
        if (context is LifecycleOwner) {
            context.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun clear() {
                    finish()
                }
            })
        }
    }

    fun postWhenPrepared(runnable: (MediaPlayer) -> Unit) = resolver.postWhenPrepared(runnable)


}