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

        val manager = MediaManagerFactory.getMediaManager(this)

        contentView!!.resume_button.setOnClickListener {
            manager.resume()
        }

        contentView!!.pause_button.setOnClickListener {
            manager.pause()
        }

        contentView!!.stop_button.setOnClickListener {
            manager.stop()
        }

        contentView!!.seek_button.setOnClickListener {
            manager.seekTo(2000)
        }

        disposable = manager.getMediaInfoObservable().subscribe {
            println(it)
        }
        if (savedInstanceState == null) {
            manager.loadExternalFileMusic("Music/stephen_stay.mp3")
        }

    }
    // internal, seekTo, czas całkowity

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
