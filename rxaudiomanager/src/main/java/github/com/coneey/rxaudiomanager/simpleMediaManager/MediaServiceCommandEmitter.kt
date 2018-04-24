package github.com.coneey.rxaudiomanager.simpleMediaManager

import android.media.AudioAttributes
import github.com.coneey.rxaudiomanager.serviceMediaManager.ServiceCommand
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

object MediaServiceCommandEmitter : MediaManager {


    val commandSubject: Subject<ServiceCommand> = BehaviorSubject.create()

    override fun loadStreamMusic(url: String, attributes: AudioAttributes?) {
        commandSubject.onNext(ServiceCommand.LoadStreamMusic(url, attributes))
    }

    override fun loadResourceMusic(resourceId: Int, attributes: AudioAttributes?) {
        commandSubject.onNext(ServiceCommand.LoadResourceMusic(resourceId, attributes))
    }

    override fun finish() {
        println("FINALIZE COMMAND!")
         commandSubject.onNext(ServiceCommand.Stop())
    }


}