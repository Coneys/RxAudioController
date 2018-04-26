package github.com.coneey.rxaudiomanager.serviceMediaManager

import android.media.AudioAttributes
import github.com.coneey.rxaudiomanager.mediaListener.Millisecond

sealed class ServiceCommand {
    class Stop : ServiceCommand()
    class Pause : ServiceCommand()
    class Resume : ServiceCommand()
    class Finish : ServiceCommand()
    class SeekTo(millisecond: Millisecond) : ServiceCommand()
    class LoadStreamMusic(val url: String, val attr: AudioAttributes?) : ServiceCommand()
    class LoadResourceMusic(val resourceId: Int, val attr: AudioAttributes?) : ServiceCommand()
    class LoadExternalFileMusic(val filePath: String, val attr: AudioAttributes?) : ServiceCommand()
    class LoadInternalFileMusic(val filePath: String, val attr: AudioAttributes?) : ServiceCommand()
}