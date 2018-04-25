package github.com.coneey.rxaudiomanager.serviceMediaManager

import android.media.AudioAttributes

sealed class ServiceCommand {
    class Stop : ServiceCommand()
    class Pause : ServiceCommand()
    class Resume : ServiceCommand()
    class Finish : ServiceCommand()
    class LoadStreamMusic(val url: String, val attr: AudioAttributes?) : ServiceCommand()
    class LoadResourceMusic(val resourceId: Int, val attr: AudioAttributes?) : ServiceCommand()
    class LoadFileMusic(val filePath: String, val attr: AudioAttributes?) : ServiceCommand()
}