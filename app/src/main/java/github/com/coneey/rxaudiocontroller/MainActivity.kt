package github.com.coneey.rxaudiocontroller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import github.com.coneey.rxaudiomanager.MediaManagerFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            println("CREATING SERVICE!")
            val manager = MediaManagerFactory.getServiceMediaManager(this)
            manager.loadStreamMusic("https://www.ssaurel.com/tmp/mymusic.mp3")
        }
    }
}
