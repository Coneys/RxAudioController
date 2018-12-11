package github.com.coneey.rxaudiocontroller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import github.com.coneey.rxaudiomanager.MediaManagerFactory
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    val manager by lazy { MediaManagerFactory.getServiceMediaManager(this) }

    var disposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        resume_button.setOnClickListener {
            manager.resume()
        }

        pause_button.setOnClickListener {
            manager.pause()
        }

        stop_button.setOnClickListener {
            manager.stop()
        }

        stream_button.setOnClickListener {
            manager.loadStreamMusic("http://janowlubelski.treespot.pl/media/get/149")
            manager.start()
        }

        stream2_button.setOnClickListener {
            manager.loadStreamMusic("http://janowlubelski.treespot.pl/media/get/147")
            manager.start()
        }

        disposable = manager.getMediaInfoObservable().subscribe {
            println(it)
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing)
            manager.finish()
        disposable?.dispose()

    }
}
