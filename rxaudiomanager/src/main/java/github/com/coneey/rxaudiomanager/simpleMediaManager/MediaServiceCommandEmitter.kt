package github.com.coneey.rxaudiomanager.simpleMediaManager

import android.media.AudioAttributes
import github.com.coneey.rxaudiomanager.mediaListener.MediaInfo
import github.com.coneey.rxaudiomanager.serviceMediaManager.ServiceCommand
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

object MediaServiceCommandEmitter : MediaManager {

    val mediaInfoSubject: Subject<MediaInfo> = BehaviorSubject.create()
    val commandSubject: Subject<ServiceCommand> = BehaviorSubject.create()


    override fun getMediaInfoObservable(): Observable<MediaInfo> = mediaInfoSubject


    override fun loadStreamMusic(url: String, attributes: AudioAttributes?) {
        commandSubject.onNext(ServiceCommand.LoadStreamMusic(url, attributes))
    }

    override fun loadResourceMusic(resourceId: Int, attributes: AudioAttributes?) {
        commandSubject.onNext(ServiceCommand.LoadResourceMusic(resourceId, attributes))
    }

    override fun loadExternalFileMusic(filePath: String, attributes: AudioAttributes?) {
        commandSubject.onNext(ServiceCommand.LoadFileMusic(filePath, attributes))
    }

    override fun finish() {
        commandSubject.onNext(ServiceCommand.Finish())
    }

    override fun pause() {
        commandSubject.onNext(ServiceCommand.Pause())
    }

    override fun resume() {
        commandSubject.onNext(ServiceCommand.Resume())
    }

    override fun stop() {
        commandSubject.onNext(ServiceCommand.Stop())
    }


}