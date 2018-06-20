package github.com.coneey.rxaudiocontroller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import github.com.coneey.rxaudiomanager.MediaManagerFactory
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.view.*
import org.jetbrains.anko.contentView

class MainActivity : AppCompatActivity() {

    var disposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = MediaManagerFactory.getServiceMediaManager(this)

        contentView!!.resume_button.setOnClickListener {
            manager.resume()
        }

        contentView!!.pause_button.setOnClickListener {
            manager.pause()
        }

        contentView!!.stop_button.setOnClickListener {
            manager.stop()
        }

        contentView!!.stream_button.setOnClickListener {
            manager.loadStreamMusic("http://janowlubelski.treespot.pl/media/get/149")
            manager.start()
        }

        disposable = manager.getMediaInfoObservable().subscribe {
            println(it)
        }
        if (savedInstanceState == null) {

            manager.useService {
                manager.loadExternalFileMusic("Music/stephen_stay.mp3")
                manager.start()
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
