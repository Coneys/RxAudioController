package github.com.coneey.rxaudiomanager.serviceMediaManager

import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import github.com.coneey.rxaudiomanager.simpleMediaManager.MediaManager
import github.com.coneey.rxaudiomanager.simpleMediaManager.MediaServiceCommandEmitter
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.startService

typealias ServiceRunnable = (ServiceMediaManager) -> Unit

class ServiceMediaManager(val context: Context) : MediaManager by MediaServiceCommandEmitter {

    private var observingServiceStateDisposable: Disposable? = null
    private var serviceListening = false
    private val runnables: MutableList<ServiceRunnable> = ArrayList()

    init {
        context.startService<PlayerService>()
        println("SERVICE TEST - CREATING")
        observingServiceStateDisposable = PlayerService.listening.subscribe {
            println("SERVICE TEST - SOMETHING ARRIVED $it")

            serviceListening = it
            if (it) {
                executeAndClear()
            }
        }
    }

    private fun executeAndClear() {
        runnables.forEach { it(this) }
        runnables.clear()
    }


    override fun loadStreamMusic(url: String, attributes: AudioAttributes?) {
        println("SERVICE TEST - LOADING STREAM MUSIC")
        MediaServiceCommandEmitter.loadStreamMusic(url, attributes)
    }


    override fun loadExternalFileMusic(filePath: String, attributes: AudioAttributes?) {

        if (context is AppCompatActivity) {
            val permisstions = RxPermissions(context)
            permisstions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .take(1)
                    .doOnNext { println("SOMETHING ARRIVBES IN PERMISSION $it") }
                    .subscribe { if (it) MediaServiceCommandEmitter.loadExternalFileMusic(filePath, attributes) } // FIXME add disposable?
        } else throw RuntimeException("Context has to be AppCompatActivity!")

    }

    fun useService(func: ServiceRunnable) {
        println("TRYTING TO USE SERVICE BUT $serviceListening")
        if (serviceListening) {
            func.invoke(this)
        } else {
            runnables.add(func)
        }
    }

    override fun finish() {
        observingServiceStateDisposable?.dispose()
        MediaServiceCommandEmitter.finish()
    }


}