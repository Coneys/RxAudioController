package github.com.coneey.rxaudiomanager.serviceMediaManager

import android.media.AudioAttributes

sealed class ServiceCommand {
    class Stop : ServiceCommand()
    class LoadStreamMusic(val url: String, val attr: AudioAttributes?) : ServiceCommand()
    class LoadResourceMusic(val resourceId: Int, val attr: AudioAttributes?) : ServiceCommand()
}