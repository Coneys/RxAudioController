package github.com.coneey.rxaudiomanager.serviceMediaManager

import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import github.com.coneey.rxaudiomanager.mediaListener.MediaInfo
import github.com.coneey.rxaudiomanager.mediaListener.Millisecond
import github.com.coneey.rxaudiomanager.simpleMediaManager.MediaManager
import github.com.coneey.rxaudiomanager.simpleMediaManager.MediaServiceCommandEmitter
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.startService

typealias ServiceRunnable = (ServiceMediaManager) -> Unit

class ServiceMediaManager(val context: Context) : MediaManager {


    private var observingServiceStateDisposable: Disposable? = null
    private val runnables: MutableList<ServiceRunnable> = ArrayList()

    init {
        restartService()
    }

    fun restartService() {
        context.startService<PlayerService>()
        println("SERVICE TEST - CREATING")
        observingServiceStateDisposable = PlayerService.listening.subscribe {
            println("SERVICE TEST - SOMETHING ARRIVED $it")

            if (it) {
                executeAndClear()
            }
        }
    }

    private fun executeAndClear() {
        runnables.forEach { it(this) }
        runnables.clear()
    }

    override fun start() {

        useService {
            MediaServiceCommandEmitter.start()
        }
    }


    override fun loadStreamMusic(url: String, attributes: AudioAttributes?) {
        useService {
            MediaServiceCommandEmitter.loadStreamMusic(url, attributes)

        }
    }


    override fun loadExternalFileMusic(filePath: String, attributes: AudioAttributes?) {
        useService {
            if (context is AppCompatActivity) {
                val permisstions = RxPermissions(context)
                permisstions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .take(1)
                        .subscribe { if (it) MediaServiceCommandEmitter.loadExternalFileMusic(filePath, attributes) } // FIXME add disposable?
            } else throw RuntimeException("Context has to be AppCompatActivity!")
        }
    }

    private fun useService(func: ServiceRunnable) {
        println("TRYTING TO USE SERVICE BUT ${PlayerService.isListening}")
        if (PlayerService.isListening) {
            func.invoke(this)
        } else {
            restartService()
            runnables.add(func)
        }
    }

    override fun finish() {
        useService {
            MediaServiceCommandEmitter.finish()
        }
        observingServiceStateDisposable?.dispose()

    }

    override fun loadResourceMusic(resourceId: Int, attributes: AudioAttributes?) {
        useService { MediaServiceCommandEmitter.loadResourceMusic(resourceId, attributes) }
    }

    override fun loadInternalFileMusic(filePath: String, attributes: AudioAttributes?) {
        useService { MediaServiceCommandEmitter.loadInternalFileMusic(filePath, attributes) }
    }

    override fun seekTo(millisecond: Millisecond) {
        useService { MediaServiceCommandEmitter.seekTo(millisecond) }
    }

    override fun pause() {
        useService { MediaServiceCommandEmitter.pause() }
    }

    override fun resume() {
        useService { MediaServiceCommandEmitter.resume() }
    }

    override fun stop() {
        useService { MediaServiceCommandEmitter.stop() }
    }

    override fun reset() {
        useService { MediaServiceCommandEmitter.reset() }
    }

    override fun getMediaInfoObservable(): Observable<MediaInfo> = MediaServiceCommandEmitter.getMediaInfoObservable()

}