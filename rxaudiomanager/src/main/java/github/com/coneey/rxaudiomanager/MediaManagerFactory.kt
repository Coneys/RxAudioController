package github.com.coneey.rxaudiomanager

import android.content.Context
import android.media.MediaPlayer
import github.com.coneey.rxaudiomanager.serviceMediaManager.ServiceMediaManager
import github.com.coneey.rxaudiomanager.simpleMediaManager.SimpleMediaManager

class MediaManagerFactory {


    companion object {
        fun getMediaManager(context: Context, player: MediaPlayer = MediaPlayer()) = SimpleMediaManager(context, player)
        fun getServiceMediaManager(context: Context) = ServiceMediaManager(context)

    }

}
