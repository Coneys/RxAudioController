package github.com.coneey.rxaudiomanager.serviceMediaManager

import android.media.AudioAttributes
import github.com.coneey.rxaudiomanager.mediaListener.Millisecond

sealed class ServiceCommand {
    object Stop : ServiceCommand()
    object Pause : ServiceCommand()
    object Resume : ServiceCommand()
    object Finish : ServiceCommand()
    object Restart : ServiceCommand()
    class SeekTo(val millisecond: Millisecond) : ServiceCommand()
    class LoadStreamMusic(val url: String, val attr: AudioAttributes?) : ServiceCommand()
    class LoadResourceMusic(val resourceId: Int, val attr: AudioAttributes?) : ServiceCommand()
    class LoadExternalFileMusic(val filePath: String, val attr: AudioAttributes?) : ServiceCommand()
    class LoadInternalFileMusic(val filePath: String, val attr: AudioAttributes?) : ServiceCommand()
}