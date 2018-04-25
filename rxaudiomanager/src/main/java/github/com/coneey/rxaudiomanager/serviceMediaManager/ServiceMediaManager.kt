package github.com.coneey.rxaudiomanager.serviceMediaManager

import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import github.com.coneey.rxaudiomanager.simpleMediaManager.MediaManager
import github.com.coneey.rxaudiomanager.simpleMediaManager.MediaServiceCommandEmitter
import org.jetbrains.anko.startService

class ServiceMediaManager(val context: Context) : MediaManager by MediaServiceCommandEmitter {
    init {
        context.startService<PlayerService>()
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


}