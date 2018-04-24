package github.com.coneey.rxaudiomanager.serviceMediaManager

import android.content.Context
import github.com.coneey.rxaudiomanager.simpleMediaManager.MediaManager
import github.com.coneey.rxaudiomanager.simpleMediaManager.MediaServiceCommandEmitter
import org.jetbrains.anko.startService

class ServiceMediaManager(context: Context) : MediaManager by MediaServiceCommandEmitter {
    init {
        context.startService<PlayerService>()
    }


}